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
public final class BookTable implements Serializable {
    private final HashMap accounts = new HashMap();
    private final HashMap transactions = new HashMap();

    double getHeldBalance(final String id, final Date current) {
        if (!accounts.containsKey(id))
            return 0;
        return ((PrevalentBook) accounts.get(id)).getHeldBalance(current);

    }

    PrevalentBook getBook(final String id) {
        return (PrevalentBook) accounts.get(id);
    }

    void addBook(PrevalentBook book) {
        accounts.put(book.getId(), book);
    }

    public boolean exists(String id) {
        return transactions.containsKey(id);
    }

    public PostedHeldTransaction getHeld(String id) {
        return (PostedHeldTransaction) transactions.get(id);
    }

    void add(PostedHeldTransaction tran) throws TransactionExistsException {
        if (transactions.containsKey(tran.getRequestId()))
            throw new TransactionExistsException(null, tran.getRequestId());
        Iterator items = tran.getItems();
        while (items.hasNext()) {
            TransactionItem item = (TransactionItem) items.next();
            PrevalentBook book = (PrevalentBook) accounts.get(item.getBook());
            book.add(tran);
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
            PrevalentBook held = (PrevalentBook) accounts.get(item.getBook());
            if (held != null) {
                held.expire(tran);
                if (held.getHoldCount() == 0)
                    accounts.remove(item.getBook());
            }
        }
        transactions.remove(tran.getRequestId());

    }

    public double getTestBalance() {

        return 0;
    }
}
