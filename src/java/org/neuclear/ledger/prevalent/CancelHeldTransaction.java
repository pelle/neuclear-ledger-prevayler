package org.neuclear.ledger.prevalent;


import org.neuclear.ledger.PostedHeldTransaction;

import java.util.Date;


/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Mar 20, 2004
 * Time: 1:31:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class CancelHeldTransaction implements org.prevayler.TransactionWithQuery {

    final PostedHeldTransaction tran;


    CancelHeldTransaction(PostedHeldTransaction tran) {
        this.tran = tran;
    }


    /**
     * This method is called by Prevayler.execute(Transaction) to execute this Transaction on the given Prevalent System. See org.prevayler.demos for usage examples.
     *
     * @param prevalentSystem The system on which this Transaction will execute.
     * @param executionTime   The time at which this Transaction is being executed. Every Transaction executes completely within a single moment in time. Logically, a Prevalent System's time does not pass during the execution of a Transaction.
     */
    public Object executeAndQuery(Object prevalentSystem, Date executionTime) {
        LedgerSystem system = (LedgerSystem) prevalentSystem;
        BookTable table = system.getBookTable();
        System.out.println("Execute Cancel");
        table.expire(tran);
        return executionTime;
    }
}
