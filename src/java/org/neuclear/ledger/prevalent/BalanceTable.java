package org.neuclear.ledger.prevalent;

import gnu.trove.TDoubleProcedure;
import gnu.trove.TObjectDoubleHashMap;
import org.neuclear.ledger.LowlevelLedgerException;

import java.io.Serializable;

/**
 * This contains the balances of all the accounts
 */
public final class BalanceTable implements Serializable {
    private final TObjectDoubleHashMap balances = new TObjectDoubleHashMap();

    double getBalance(final String id) {
        if (!balances.containsKey(id))
            return 0;
        return balances.get(id);
    }

    double add(final String id, final double amount) {
        if (!balances.containsKey(id))
            return balances.put(id, amount);
        final double nb = balances.get(id) + amount;
        return balances.put(id, nb);
    }

    double getTestBalance() throws LowlevelLedgerException {
        TestBalanceCalculator calc = new TestBalanceCalculator();
        balances.forEachValue(calc);
        return calc.getBalance();
    }

    private static class TestBalanceCalculator implements TDoubleProcedure {
        private double balance = 0;

        /**
         * Executes this procedure. A false return value indicates that
         * the application executing this procedure should not invoke this
         * procedure again.
         *
         * @param value a value of type <code>double</code>
         * @return true if additional invocations of the procedure are
         *         allowed.
         */
        public boolean execute(double value) {
            balance += value;
            return true;
        }

        private double getBalance() {
            return balance;
        }

    }
}
