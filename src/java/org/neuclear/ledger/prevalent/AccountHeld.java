package org.neuclear.ledger.prevalent;

import org.neuclear.ledger.PostedHeldTransaction;
import org.neuclear.ledger.TransactionItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Mar 20, 2004
 * Time: 5:49:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccountHeld implements Serializable {
    private List holds = new ArrayList(1);
    private HoldTable holdtable;
    private String id;

    public AccountHeld(HoldTable holdtable, String id) {
        this.holdtable = holdtable;
        this.id = id;
    }

    public void add(final PostedHeldTransaction tran) {
        holds.add(tran);
    }

    double getBalance(final Date current) {
        Iterator iter = holds.iterator();
        double balance = 0;
        PostedHeldTransaction expired = null;
        while (iter.hasNext()) {
            PostedHeldTransaction transaction = (PostedHeldTransaction) iter.next();
            if (transaction.getExpiryTime().after(current)) {
                if (transaction.getReceiptId() != null) {
                    Iterator items = transaction.getItems();
                    while (items.hasNext()) {
                        TransactionItem item = (TransactionItem) items.next();
                        if (item.getBook().equals(id) && item.getAmount() < 0)
                            balance += item.getAmount();
                    }
                }
            } else {
                expired = transaction;
            }
        }
        if (expired != null)
            holdtable.expire(expired);
        return balance;
    }

    void expire(final PostedHeldTransaction dead) {
        for (int i = 0; i < holds.size(); i++) {
            PostedHeldTransaction transaction = (PostedHeldTransaction) holds.get(i);
            if (transaction.getRequestId().equals(dead.getRequestId())) {
                holds.remove(i);
                return;
            }

        }
        holds.remove(dead);
    }

    int getHoldCount() {
        return holds.size();
    }
}
