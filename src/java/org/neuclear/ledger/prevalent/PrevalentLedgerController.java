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
public class PrevalentLedgerController extends LedgerController implements Serializable {
    private final LedgerSystem system;

    /**
     * Persistent Prevalent Ledger
     *
     * @param id
     * @param basedir
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public PrevalentLedgerController(final String id, final String basedir) throws IOException, ClassNotFoundException {
        super(id);
        prevayler = PrevaylerFactory.createPrevayler(new LedgerSystem(id), basedir);
        system = (LedgerSystem) prevayler.prevalentSystem();
    }

    /**
     * Transient PrevalentLedger
     *
     * @param id
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public PrevalentLedgerController(final String id) throws IOException, ClassNotFoundException {
        super(id);
        prevayler = PrevaylerFactory.createTransientPrevayler(new LedgerSystem(id));
        system = (LedgerSystem) prevayler.prevalentSystem();
    }

    public boolean existsLedger(String id) {
        return false;
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
    public Date getTransactionTime(String id) throws LowlevelLedgerException, UnknownTransactionException {
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

    public double getBalance(String ledger, String book) throws LowlevelLedgerException {
        try {
            return ((Double) prevayler.execute(new GetBalanceQuery(ledger, book))).doubleValue();
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

    public double getAvailableBalance(String ledger, String book) throws LowlevelLedgerException {
        try {
            return ((Double) prevayler.execute(new GetAvailableBalanceQuery(ledger, book))).doubleValue();
        } catch (Exception e) {
            throw new LowlevelLedgerException(e);
        }
    }

    public long getBookCount(String ledger) throws LowlevelLedgerException {
        return 0;  //TODO Implement
    }

    public long getTransactionCount(String ledger) throws LowlevelLedgerException {
        return 0;  //TODO Implement
    }

    public boolean transactionExists(String id) throws LowlevelLedgerException {
        try {
            return ((Boolean) prevayler.execute(new DoesTransactionExist(id))).booleanValue();
        } catch (Exception e) {
            throw new LowlevelLedgerException(e);
        }
    }

    public boolean heldTransactionExists(String id) throws LowlevelLedgerException {
        try {
            return ((Boolean) prevayler.execute(new DoesHeldTransactionExist(id))).booleanValue();
        } catch (Exception e) {
            throw new LowlevelLedgerException(e);
        }
    }

    /**
     * Register a Book in the system
     *
     * @param id
     * @param nickname
     * @param type
     * @param source
     * @param registrationid
     * @return
     * @throws org.neuclear.ledger.LowlevelLedgerException
     *
     */
    public Book registerBook(String id, String nickname, String type, String source, String registrationid) throws LowlevelLedgerException {
        try {
            return (Book) prevayler.execute(new RegisterBook(id, nickname, type, source, registrationid));
        } catch (Exception e) {
            if (e instanceof LowlevelLedgerException)
                throw (LowlevelLedgerException) e;
            throw new LowlevelLedgerException(e);
        }
    }

    public Book getBook(String id) throws LowlevelLedgerException {
        try {
            return (Book) prevayler.execute(new GetBook(id));
        } catch (Exception e) {
            if (e instanceof LowlevelLedgerException)
                throw (LowlevelLedgerException) e;
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
        try {
            final PostedHeldTransaction t = (PostedHeldTransaction) prevayler.execute(new FindHeldTransaction(idstring));
            return t;
        } catch (Exception e) {
            if (e instanceof UnknownTransactionException)
                throw (UnknownTransactionException) e;
            throw new LowlevelLedgerException(e);
        }
    }

    public void setReceiptId(String id, String receipt) throws LowlevelLedgerException, UnknownTransactionException {
        prevayler.execute(new SetReceiptId(id, receipt));

    }

    public void setHeldReceiptId(String id, String receipt) throws LowlevelLedgerException, UnknownTransactionException {
        prevayler.execute(new SetHeldReceiptId(id, receipt));
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
    public Date performCancelHold(PostedHeldTransaction hold) throws LowlevelLedgerException, UnknownTransactionException {
        try {
            System.out.println("Perform Cancel");

            return (Date) prevayler.execute(new CancelHeldTransaction(hold));
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

    public double getTestBalance(String ledger) throws LowlevelLedgerException {
        try {
            return ((Double) prevayler.execute(new GetTestBalanceQuery(ledger))).doubleValue();
        } catch (Exception e) {
            throw new LowlevelLedgerException(e);
        }
    }

    public void close() {
        try {
//            prevayler.takeSnapshot();
            prevayler.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private Prevayler prevayler;
}
