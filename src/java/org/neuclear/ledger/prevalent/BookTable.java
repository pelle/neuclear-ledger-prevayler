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

import org.neuclear.ledger.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This contains the balances of all the accounts
 */
public final class BookTable implements Serializable {
    private final HashMap accounts;
    private final HashMap held;

    public BookTable() {
        accounts = new HashMap();
        held = new HashMap();
    }


    PrevalentBook getBook(final String id) {
//        printBalances();
//        System.out.println("Getting "+id);
        return (PrevalentBook) accounts.get(id);
    }

    void add(PrevalentBook book) {
//        System.out.println("Adding "+book.getId());
        accounts.put(book.getId(), book);
//        printBalances();
//        System.out.println("Added Book");
    }

    public boolean heldExists(String id) {
        return held.containsKey(id);
    }

    public PostedHeldTransaction getHeld(String id) {
        return (PostedHeldTransaction) held.get(id);
    }

    void add(PostedHeldTransaction tran) throws TransactionExistsException {
        if (held.containsKey(tran.getRequestId()))
            throw new TransactionExistsException(null, tran.getRequestId());
        Iterator items = tran.getItems();
        while (items.hasNext()) {
            TransactionItem item = (TransactionItem) items.next();
            Book book = (Book) item.getBook();
            getBook(book.getId()).add(tran);
        }
        held.put(tran.getRequestId(), tran);
//        printBalances();
    }

    void add(UnPostedTransaction tran) throws TransactionExistsException {
        Iterator items = tran.getItems();
        while (items.hasNext()) {
            TransactionItem item = (TransactionItem) items.next();
            Book book = (Book) item.getBook();
            getBook(book.getId()).add(item.getAmount());
        }
//        printBalances();
    }


    double getTestBalance() {
        double balance = 0;
        Iterator iter = accounts.entrySet().iterator();
        while (iter.hasNext()) {
            PrevalentBook book = (PrevalentBook) ((Map.Entry) iter.next()).getValue();
            balance += book.getBalance();
        }
        return balance;
    }

    void printBalances() {
        Iterator iter = accounts.entrySet().iterator();
        while (iter.hasNext()) {
            PrevalentBook book = (PrevalentBook) ((Map.Entry) iter.next()).getValue();
            System.out.println(book);
        }

    }

    void expire(PostedHeldTransaction tran) {
        PrevalentBook.expire(tran, this);
    }
}
