package org.neuclear.ledger.prevalent;

import org.neuclear.ledger.*;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Mar 20, 2004
 * Time: 1:03:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrevalentLedger extends Ledger implements Serializable {
    private final LedgerSystem system;

    public PrevalentLedger(final String id, final String basedir) throws IOException, ClassNotFoundException {
        super(id);
        prevayler = PrevaylerFactory.createPrevayler(new LedgerSystem(id), basedir);
        system = (LedgerSystem) prevayler.prevalentSystem();
    }

    /**
     * The basic interface for creating Transactions in the database.
     * The implementing class takes this transacion information and stores it with an automatically generated uniqueid.
     * This id is returned as an identifier of the transaction.
     *
     * @param trans Transaction to perform
     * @return Unique ID
     */
    public PostedTransaction performTransaction(UnPostedTransaction trans) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException {
        try {
            return (PostedTransaction) prevayler.execute(new PostTransaction(trans));
        } catch (Exception e) {
            if (e instanceof InvalidTransactionException)
                throw (InvalidTransactionException) e;
            if (e instanceof UnBalancedTransactionException)
                throw (UnBalancedTransactionException) e;
            if (e instanceof LowlevelLedgerException)
                throw (LowlevelLedgerException) e;
            throw new LowlevelLedgerException(e);
        }
    }

    /**
     * Similar to a transaction but guarantees that there wont be any negative balances left after the transaction.
     *
     * @param trans Transaction to perform
     * @return The reference to the transaction
     */
    public PostedTransaction performVerifiedTransfer(UnPostedTransaction trans) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException {
        try {
            return (PostedTransaction) prevayler.execute(new PostVerifiedTransfer(trans));
        } catch (Exception e) {
            if (e instanceof InvalidTransactionException)
                throw (InvalidTransactionException) e;
            if (e instanceof UnBalancedTransactionException)
                throw (UnBalancedTransactionException) e;
            if (e instanceof LowlevelLedgerException)
                throw (LowlevelLedgerException) e;
            throw new LowlevelLedgerException(e);
        }
    }

    /**
     * The basic interface for creating Transactions in the database.
     * The implementing class takes this transacion information and stores it with an automatically generated uniqueid.
     * This id is returned as an identifier of the transaction.
     *
     * @param trans Transaction to perform
     * @return Unique ID
     */
    public PostedHeldTransaction performHeldTransfer(UnPostedHeldTransaction trans) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException {
        try {
            return (PostedHeldTransaction) prevayler.execute(new PostHeldTransaction(trans));
        } catch (Exception e) {
            if (e instanceof InvalidTransactionException)
                throw (InvalidTransactionException) e;
            if (e instanceof UnBalancedTransactionException)
                throw (UnBalancedTransactionException) e;
            if (e instanceof LowlevelLedgerException)
                throw (LowlevelLedgerException) e;
            throw new LowlevelLedgerException(e);
        }
    }

    /**
     * Searches for a Transaction based on its Transaction ID
     *
     * @param id A valid ID
     * @return The Transaction object
     */
    public Date getTransactionTime(String id) throws LowlevelLedgerException, UnknownTransactionException, InvalidTransactionException, UnknownBookException {
        if (system.getTransactionTable().exists(id))
            return new Date(system.getTransactionTable().getTransactionTime(id));
        throw new UnknownTransactionException(this, id);
    }

    /**
     * Calculate the true accounting balance at a given time. This does not take into account any held transactions, thus may not necessarily
     * show the Available balance.<p>
     * Example sql for implementors: <pre>
     * select c.credit - d.debit from
     *      (
     *          select sum(amount) as credit
     *          from ledger
     *          where transactiondate <= sysdate and end_date is null and credit= 'neu://bob'
     *       ) c,
     *      (
     *          select sum(amount) as debit
     *          from ledger
     *          where transactiondate <= sysdate and end_date is null and debit= 'neu://bob'
     *       ) d
     * <p/>
     * </pre>
     *
     * @return the balance as a double
     */

    public double getBalance(String book) throws LowlevelLedgerException {
        try {
            return ((Double) prevayler.execute(new GetBalanceQuery(book))).doubleValue();
        } catch (Exception e) {
            throw new LowlevelLedgerException(e);
        }
    }

    /**
     * Calculate the available balance at a given time. This DOES take into account any held transactions.
     * Example sql for implementors: <pre>
     * select c.credit - d.debit from
     *      (
     *          select sum(amount) as credit
     *          from ledger
     *          where transactiondate <= sysdate and (end_date is null or end_date>= sysdate) and credit= 'neu://bob'
     *       ) c,
     *      (
     *          select sum(amount) as debit
     *          from ledger
     *          where transactiondate <= sysdate and end_date is null and debit= 'neu://bob'
     *       ) d
     * <p/>
     * </pre>
     *
     * @return the balance as a double
     */

    public double getAvailableBalance(String book) throws LowlevelLedgerException {
        try {
            return ((Double) prevayler.execute(new GetAvailableBalanceQuery(book))).doubleValue();
        } catch (Exception e) {
            throw new LowlevelLedgerException(e);
        }
    }

    /**
     * Searches for a Held Transaction based on its Transaction ID
     *
     * @param idstring A valid ID
     * @return The Transaction object
     */
    public PostedHeldTransaction findHeldTransaction(String idstring) throws LowlevelLedgerException, UnknownTransactionException {
        return system.getHoldTable().get(idstring);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setReceiptId(String id, String receipt) throws LowlevelLedgerException, UnknownTransactionException {

    }

    /**
     * Cancels a Held Transaction.
     *
     * @param hold
     * @throws org.neuclear.ledger.LowlevelLedgerException
     *
     * @throws org.neuclear.ledger.UnknownTransactionException
     *
     */
    public void performCancelHold(PostedHeldTransaction hold) throws LowlevelLedgerException, UnknownTransactionException {
        try {
            System.out.println("Perform Cancel");

            prevayler.execute(new CancelHeldTransaction(hold));
        } catch (Exception e) {
            if (e instanceof UnknownTransactionException)
                throw (UnknownTransactionException) e;
            if (e instanceof LowlevelLedgerException)
                throw (LowlevelLedgerException) e;
            throw new LowlevelLedgerException(e);
        }
    }

    public PostedTransaction performCompleteHold(PostedHeldTransaction hold, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, TransactionExpiredException {
        try {
            return (PostedTransaction) prevayler.execute(new CompleteHeldTransaction(hold, amount, comment));
        } catch (Exception e) {
            if (e instanceof InvalidTransactionException)
                throw (InvalidTransactionException) e;
            if (e instanceof TransactionExpiredException)
                throw (TransactionExpiredException) e;
            if (e instanceof LowlevelLedgerException)
                throw (LowlevelLedgerException) e;

            throw new LowlevelLedgerException(e);
        }
    }

    public double getTestBalance() throws LowlevelLedgerException {
        try {
            return ((Double) prevayler.execute(new GetTestBalanceQuery())).doubleValue();
        } catch (Exception e) {
            throw new LowlevelLedgerException(e);
        }
    }

    public void close() {
        try {
            prevayler.takeSnapshot();
            prevayler.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private Prevayler prevayler;
}
