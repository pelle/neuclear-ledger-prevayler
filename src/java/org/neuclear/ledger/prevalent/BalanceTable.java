package org.neuclear.ledger.prevalent;

import gnu.trove.TDoubleProcedure;
import gnu.trove.THashMap;
import gnu.trove.TObjectDoubleHashMap;
import org.neuclear.ledger.Book;
import org.neuclear.ledger.Transaction;
import org.neuclear.ledger.TransactionExistsException;
import org.neuclear.ledger.TransactionItem;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/*
 *  The NeuClear Project and it's libraries are
 *  (c) 2002-2004 Antilles Software Ventures SA
 *  For more information see: http://neuclear.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/**
 * User: pelleb
 * Date: May 4, 2004
 * Time: 9:43:38 AM
 */
public class BalanceTable implements Serializable {
    public BalanceTable() {
        ledgers = new THashMap();
    }

    public double getBalance(final String ledger, final String book) {
        TObjectDoubleHashMap balances = getLedger(ledger);
        if (balances.containsKey(book))
            return balances.get(book);
        return 0;
    }

    private void add(final String ledger, final String book, final double amount) {
        TObjectDoubleHashMap balances = getLedger(ledger);
        if (balances.containsKey(book))
            balances.adjustValue(book, amount);
        else
            balances.put(book, amount);
    }

    private TObjectDoubleHashMap getLedger(final String ledger) {
        if (ledgers.containsKey(ledger))
            return (TObjectDoubleHashMap) ledgers.get(ledger);
        final TObjectDoubleHashMap map = new TObjectDoubleHashMap();
        ledgers.put(ledger, map);
        return map;
    }

    void add(Transaction tran) throws TransactionExistsException {
        Iterator items = tran.getItems();
        while (items.hasNext()) {
            TransactionItem item = (TransactionItem) items.next();
            Book book = (Book) item.getBook();
            add(tran.getLedger(), book.getId(), item.getAmount());
        }
//        printBalances();
    }


    public double getTestBalance(final String ledger) {
        if (!ledgers.containsKey(ledger))
            return 0;
        TObjectDoubleHashMap balances = (TObjectDoubleHashMap) ledgers.get(ledger);
        CalculateTestBalance calc = new CalculateTestBalance();
        balances.forEachValue(calc);
        return calc.getBalance();
    }

    private final Map ledgers;

    private static class CalculateTestBalance implements TDoubleProcedure {
        private double balance = 0;

        public boolean execute(double b) {
            balance += b;
            return true;
        }

        public double getBalance() {
            return balance;
        }
    }

}
