package org.neuclear.ledger.prevalent;

import org.prevayler.TransactionWithQuery;

import java.util.Date;

/*
NeuClear Distributed Transaction Clearing Platform
(C) 2003 Pelle Braendgaard

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

$Id: GetBook.java,v 1.1 2004/04/20 00:17:56 pelle Exp $
$Log: GetBook.java,v $
Revision 1.1  2004/04/20 00:17:56  pelle
Refactored to use PrevalentBook and BookTable for most stuff.
Added new Transactions for dealing with books.
There is still some sort of serialization error.

Revision 1.1  2004/04/06 22:56:36  pelle
Updated with new API Changes

*/

/**
 * User: pelleb
 * Date: Apr 6, 2004
 * Time: 9:30:44 PM
 */
public class GetBook implements TransactionWithQuery {
    public GetBook(String id) {
        this.id = id;
    }

    private String id;

    /**
     * @param prevalentSystem The Prevalent System to be queried.
     * @param executionTime   The "current" time.
     * @return The result of this Query.
     * @throws Exception Any Exception encountered by this Query.
     */
    public Object executeAndQuery(Object prevalentSystem, Date executionTime) throws Exception {
        LedgerSystem system = (LedgerSystem) prevalentSystem;
        PrevalentBook book = system.getBookTable().getBook(id);
        if (book == null) {
            book = new PrevalentBook(id, executionTime, system.getBookTable());
            system.getBookTable().addBook(book);
        }
        return book;
    }
}
