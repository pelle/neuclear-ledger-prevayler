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

import org.prevayler.TransactionWithQuery;

import java.util.Date;

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
            system.getBookTable().add(book);
        }
        return book.createBook();
    }
}

