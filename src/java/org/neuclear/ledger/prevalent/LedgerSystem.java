package org.neuclear.ledger.prevalent;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Mar 20, 2004
 * Time: 1:51:55 PM
 * To change this template use File | Settings | File Templates.
 */
public final class LedgerSystem implements Serializable {
    private BookTable books = new BookTable();
    private TransactionTable transactions = new TransactionTable();
    private String ledgerid;
    private BalanceTable balances = new BalanceTable();

    LedgerSystem(String ledgerid) {
        this.ledgerid = ledgerid;
    }

    final String getLedgerId() {
        return ledgerid;
    }


    final TransactionTable getTransactionTable() {
        return transactions;
    }

    final BookTable getBookTable() {
        return books;
    }

    final double getBalance(final String ledger, final String bookid) {
        return balances.getBalance(ledger, bookid);
    }

    final double getAvailableBalance(final String ledger, final String bookid, final Date executionTime) {
        return getHeldBalance(ledger, bookid, executionTime) + getBalanceTable().getBalance(ledger, bookid);
    }

    final double getHeldBalance(final String ledger, final String bookid, final Date executionTime) {
        PrevalentBook book = books.getBook(bookid);
        if (book == null)
            return 0.0;
        return book.getHeldBalance(ledger, executionTime);
    }

    final BalanceTable getBalanceTable() {
        return balances;
    }

}
