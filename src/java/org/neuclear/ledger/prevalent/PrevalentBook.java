package org.neuclear.ledger.prevalent;

/*
 *  The NeuClear Project and it's libraries are
 *  (c) 2002-2004 Antilles Software Ventures SA
 *  For more information see: http://neuclear.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import org.neuclear.ledger.Book;
import org.neuclear.ledger.PostedHeldTransaction;
import org.neuclear.ledger.TransactionItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Mar 20, 2004
 * Time: 5:49:35 PM
 * To change this template use File | Settings | File Templates.
 */
final class PrevalentBook extends Book implements Serializable {
    private ArrayList holds = null;
//    private double balance;
    private BookTable books;

    PrevalentBook(String id, String nickname, String type, String source, Date registered, Date updated, String registrationid, BookTable books) {
        super(id, nickname, type, source, registered, updated, registrationid);
//        balance = 0;
        this.books = books;
    }

    PrevalentBook(String id, Date registered, BookTable books) {
        super(id, registered);
        this.books = books;
    }

    final void add(final PostedHeldTransaction tran) {
        if (holds == null)
            holds = new ArrayList(1);
        holds.add(tran);
    }

//    final double add(double amount) {
//        balance += amount;
////        System.out.println("New Balance of "+getId()+" is "+balance);
//        return balance;
//    }

    final double getHeldBalance(final String ledger, final Date current) {
        if (holds == null || holds.size() == 0)
            return 0;
        Iterator iter = holds.iterator();
        double balance = 0;
        PostedHeldTransaction expired = null;
        while (iter.hasNext()) {
            PostedHeldTransaction transaction = (PostedHeldTransaction) iter.next();
            if (transaction.getExpiryTime().after(current)) {
                if (transaction.getReceiptId() != null && transaction.getLedger().equals(ledger)) {
                    Iterator items = transaction.getItems();
                    while (items.hasNext()) {
                        TransactionItem item = (TransactionItem) items.next();
                        if (item.getBook().getId().equals(id) && item.getAmount() < 0)
                            balance += item.getAmount();
                    }
                }
            } else {
                expired = transaction;
            }
        }
        if (expired != null)
            expire(expired, books);
        return balance;
    }

//    final double getAvailableBalance(Date time) {
//        return getBalance() + getHeldBalance(time);
//    }
//
//    final double getBalance() {
//        return balance;
//    }


    static final void expire(final PostedHeldTransaction dead, final BookTable books) {
        Iterator iter = dead.getItems();
        while (iter.hasNext()) {
            TransactionItem item = (TransactionItem) iter.next();
            books.getBook(item.getBook().getId()).remove(dead);
        }
    }

    private void remove(final PostedHeldTransaction dead) {
        for (int i = 0; i < holds.size(); i++) {
            PostedHeldTransaction transaction = (PostedHeldTransaction) holds.get(i);
            if (transaction.getRequestId().equals(dead.getRequestId())) {
                holds.remove(i);
                holds.trimToSize();
                return;
            }
        }
    }

    final int getHoldCount() {
        return (holds == null) ? 0 : holds.size();
    }

    void setNickname(String nickname) {
        this.nickname = nickname;
    }

    void setType(String type) {
        this.type = type;
    }

    void setSource(String source) {
        this.source = source;
    }

    void setUpdated(Date updated) {
        this.updated = updated;
    }

    void setRegistrationId(String registrationid) {
        this.registrationid = registrationid;
    }

    public String toString() {
        return "Book: " + getId();// + " Balance: " + getBalance();
    }

    Book createBook() {
        return new Book(id, nickname, type, source, registered, updated, registrationid);
    }
}
