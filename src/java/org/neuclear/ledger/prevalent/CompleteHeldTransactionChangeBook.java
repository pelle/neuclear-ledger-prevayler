package org.neuclear.ledger.prevalent;

import org.neuclear.ledger.Book;
import org.neuclear.ledger.PostedHeldTransaction;
import org.neuclear.ledger.PostedTransaction;
import org.neuclear.ledger.TransactionExistsException;
import org.prevayler.TransactionWithQuery;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Mar 20, 2004
 * Time: 1:31:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompleteHeldTransactionChangeBook implements TransactionWithQuery {

    final PostedHeldTransaction tran;
    final double amount;
    final String comment;
    final Book origbook;
    final Book newbook;


    CompleteHeldTransactionChangeBook(final PostedHeldTransaction tran, final Book origbook, final Book newbook, final double amount, final String comment) {
        this.tran = tran;
        this.amount = amount;
        this.comment = comment;
        this.origbook = origbook;
        this.newbook = newbook;
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
        BookTable holds = system.getBookTable();
        system.getBookTable().expire(tran);

        table.register(tran.getRequestId(), executionTime);

        final PostedTransaction posted = new PostedTransaction(tran, origbook, newbook, executionTime, amount, comment);
        system.getBalanceTable().add(posted);
        return posted;
    }
}
