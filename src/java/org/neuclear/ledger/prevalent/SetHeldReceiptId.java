package org.neuclear.ledger.prevalent;

import org.neuclear.ledger.PostedHeldTransaction;
import org.prevayler.Transaction;

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

$Id: SetHeldReceiptId.java,v 1.1 2004/04/06 22:56:36 pelle Exp $
$Log: SetHeldReceiptId.java,v $
Revision 1.1  2004/04/06 22:56:36  pelle
Updated with new API Changes

*/

/**
 * User: pelleb
 * Date: Apr 6, 2004
 * Time: 10:24:14 PM
 */
public class SetHeldReceiptId implements Transaction {

    public SetHeldReceiptId(String requestid, String receiptid) {
        this.requestid = requestid;
        this.receiptid = receiptid;
    }

    private String requestid;
    private String receiptid;

    /**
     * This method is called by Prevayler.execute(Transaction) to execute this Transaction on the given Prevalent System. See org.prevayler.demos for usage examples.
     *
     * @param prevalentSystem The system on which this Transaction will execute.
     * @param executionTime   The time at which this Transaction is being executed. Every Transaction executes completely within a single moment in time. Logically, a Prevalent System's time does not pass during the execution of a Transaction.
     */
    public void executeOn(Object prevalentSystem, Date executionTime) {
        LedgerSystem system = (LedgerSystem) prevalentSystem;
        HoldTable table = system.getHoldTable();
        PostedHeldTransaction tran = table.get(requestid);
        if (tran != null && tran.getReceiptId() == null)
            tran.setReceiptId(receiptid);
    }
}
