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
    private BalanceTable balances = new BalanceTable();
    private HoldTable holds = new HoldTable();
    private TransactionTable transactions = new TransactionTable();
    private String ledgerid;

    LedgerSystem(String ledgerid) {
        this.ledgerid = ledgerid;
    }

    final String getLedgerId() {
        return ledgerid;
    }


    final BalanceTable getBalanceTable() {
        return balances;
    }

    final TransactionTable getTransactionTable() {
        return transactions;
    }

    final HoldTable getHoldTable() {
        return holds;
    }

    final double getBalance(String book) {
        return balances.getBalance(book);
    }

    final double getAvailableBalance(String book, final Date executionTime) {
        return getBalance(book) + holds.getHeldBalance(book, executionTime);
    }
}
