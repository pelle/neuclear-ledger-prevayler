package org.neuclear.ledger.prevalent;

import org.neuclear.ledger.PostedHeldTransaction;
import org.neuclear.ledger.TransactionExistsException;
import org.neuclear.ledger.TransactionItem;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This contains the balances of all the accounts
 */
public final class HoldTable implements Serializable {
    private final HashMap accounts = new HashMap();
    private final HashMap transactions = new HashMap();

    double getHeldBalance(final String id, final Date current) {
        if (!accounts.containsKey(id))
            return 0;
        return ((AccountHeld) accounts.get(id)).getBalance(current);

    }

    public boolean exists(String id) {
        return transactions.containsKey(id);
    }

    public PostedHeldTransaction get(String id) {
        return (PostedHeldTransaction) transactions.get(id);
    }

    void add(PostedHeldTransaction tran) throws TransactionExistsException {
        if (transactions.containsKey(tran.getRequestId()))
            throw new TransactionExistsException(null, tran.getRequestId());
        Iterator items = tran.getItems();
        while (items.hasNext()) {
            TransactionItem item = (TransactionItem) items.next();
            AccountHeld held = (AccountHeld) accounts.get(item.getBook());
            if (held == null) {
                held = new AccountHeld(this, item.getBook());
                accounts.put(item.getBook(), held);
            }
            held.add(tran);
        }
        transactions.put(tran.getRequestId(), tran);
    }

    public void expire(PostedHeldTransaction tran) {
        if (!transactions.containsKey(tran.getRequestId()))
            return;
        System.out.println("expire");
        Iterator items = tran.getItems();
        while (items.hasNext()) {
            TransactionItem item = (TransactionItem) items.next();
            AccountHeld held = (AccountHeld) accounts.get(item.getBook());
            if (held != null) {
                held.expire(tran);
                if (held.getHoldCount() == 0)
                    accounts.remove(item.getBook());
            }
        }
        transactions.remove(tran.getRequestId());

    }
}
