package org.neuclear.ledger.prevalent;

import org.neuclear.ledger.*;
import org.prevayler.TransactionWithQuery;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Mar 20, 2004
 * Time: 1:31:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class PostHeldTransaction implements TransactionWithQuery {

    final UnPostedHeldTransaction tran;


    PostHeldTransaction(UnPostedHeldTransaction tran) {
        this.tran = tran;
    }

    /**
     * Performs the necessary modifications on the given prevalentSystem and also returns an Object or throws an Exception.
     * This method is called by Prevayler.execute(TransactionWithQuery) to execute this TransactionWithQuery on the given Prevalent System. See org.prevayler.demos for usage examples.
     *
     * @param prevalentSystem The system on which this TransactionWithQuery will execute.
     * @param executionTime   The time at which this TransactionWithQuery is being executed. Every Transaction executes completely within a single moment in time. Logically, a Prevalent System's time does not pass during the execution of a Transaction.
     */
    public Object executeAndQuery(Object prevalentSystem, Date executionTime) throws Exception {
        LedgerSystem system = (LedgerSystem) prevalentSystem;
        BookTable table = system.getBookTable();
        if (table.heldExists(tran.getRequestId()))
            throw new TransactionExistsException(null, tran.getRequestId());
//        if (table.heldExists(tran.getReceiptId()))
//            throw new TransactionExistsException(null,tran.getReceiptId());
        Iterator iter = tran.getItems();
        while (iter.hasNext()) {
            TransactionItem item = (TransactionItem) iter.next();
            if (system.getAvailableBalance(tran.getLedger(), item.getBook().getId(), executionTime) + item.getAmount() < 0)
                throw new InsufficientFundsException(null, item.getBook().getId(), item.getAmount());
        }

        PostedHeldTransaction posted = new PostedHeldTransaction(tran, executionTime);
        table.add(posted);
        return posted;
    }
}
