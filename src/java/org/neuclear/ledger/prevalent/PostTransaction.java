package org.neuclear.ledger.prevalent;

import org.neuclear.ledger.PostedTransaction;
import org.neuclear.ledger.TransactionExistsException;
import org.neuclear.ledger.TransactionItem;
import org.neuclear.ledger.UnPostedTransaction;
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
public class PostTransaction implements TransactionWithQuery {

    final UnPostedTransaction tran;


    PostTransaction(UnPostedTransaction tran) {
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
        TransactionTable table = system.getTransactionTable();
        if (table.exists(tran.getRequestId()))
            throw new TransactionExistsException(null, tran.getRequestId());
//        if (table.exists(tran.getReceiptId()))
//            throw new TransactionExistsException(null,tran.getReceiptId());

        table.register(tran.getRequestId(), executionTime);
//        table.register(tran.getReceiptId(),executionTime);

        Iterator iter = tran.getItems();
        while (iter.hasNext()) {
            TransactionItem item = (TransactionItem) iter.next();
            ((PrevalentBook) item.getBook()).add(item.getAmount());

        }

        return new PostedTransaction(tran, executionTime);
    }
}
