package org.neuclear.ledger.prevalent;

import java.io.Serializable;

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

    final double getBalance(String bookid) {
        final PrevalentBook book = books.getBook(bookid);
        if (book == null)
            return 0;
        return book.getBalance();
    }

}
