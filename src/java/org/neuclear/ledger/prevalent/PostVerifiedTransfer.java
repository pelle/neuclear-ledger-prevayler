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
public class PostVerifiedTransfer implements TransactionWithQuery {

    final UnPostedTransaction tran;


    PostVerifiedTransfer(UnPostedTransaction tran) {
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
//        if (table.heldExists(tran.getReceiptId()))
//            throw new TransactionExistsException(null, tran.getReceiptId());

        Iterator iter = tran.getItems();
        // First lets check the balances
        while (iter.hasNext()) {
            TransactionItem item = (TransactionItem) iter.next();
            final double balance = system.getAvailableBalance(tran.getLedger(), item.getBook().getId(), executionTime);
            if (item.getAmount() < 0 && balance + item.getAmount() < 0)
                throw new InsufficientFundsException(null, item.getBook().getId(), item.getAmount(), balance);
        }
        // We're ok so lets register them.
        table.register(tran.getRequestId(), executionTime);
//        table.register(tran.getReceiptId(), executionTime);

        // Now modify the balances
        system.getBalanceTable().add(tran);
        return new PostedTransaction(tran, executionTime);
    }
}
