package org.neuclear.ledger.prevalent;

import org.neuclear.ledger.Book;
import org.neuclear.ledger.PostedHeldTransaction;
import org.neuclear.ledger.TransactionItem;

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
public final class PrevalentBook extends Book {
    private List holds = null;
    private BookTable holdtable;
    private double balance = 0;

    public PrevalentBook(String id, String nickname, String type, String source, Date registered, Date updated, String registrationid, BookTable holdtable) {
        super(id, nickname, type, source, registered, updated, registrationid);
        this.holdtable = holdtable;
        this.id = id;
    }

    public PrevalentBook(String id, Date registered, BookTable holdtable) {
        super(id, registered);
        this.holdtable = holdtable;
        this.id = id;
    }

    final public void add(final PostedHeldTransaction tran) {
        if (holds == null)
            holds = new ArrayList(1);
        holds.add(tran);
    }

    final double add(double amount) {
        balance += amount;
        return balance;
    }

    final double getHeldBalance(final Date current) {
        if (holds == null || holds.size() == 0)
            return 0;
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

    final double getAvailableBalance(Date time) {
        return getBalance() + getHeldBalance(time);
    }

    final double getBalance() {
        return balance;
    }


    final void expire(final PostedHeldTransaction dead) {
        for (int i = 0; i < holds.size(); i++) {
            PostedHeldTransaction transaction = (PostedHeldTransaction) holds.get(i);
            if (transaction.getRequestId().equals(dead.getRequestId())) {
                holds.remove(i);
                return;
            }

        }
        holds.remove(dead);
    }

    final int getHoldCount() {
        return (holds == null) ? 0 : holds.size();
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public void setRegistrationId(String registrationid) {
        this.registrationid = registrationid;
    }

}
