package org.neuclear.ledger.prevalent;

import org.prevayler.Query;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Mar 22, 2004
 * Time: 10:00:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetAvailableBalanceQuery implements Query {
    public GetAvailableBalanceQuery(String ledger, String book) {
        this.bookid = book;
        this.ledger = ledger;
    }

    /**
     * @param system        The Prevalent System to be queried.
     * @param executionTime The "current" time.
     * @return The result of this Query.
     * @throws Exception Any Exception encountered by this Query.
     */
    public Object query(Object system, Date executionTime) throws Exception {
        final LedgerSystem ledgsys = ((LedgerSystem) system);
        return new Double(ledgsys.getAvailableBalance(ledger, bookid, executionTime));
    }

    private final String ledger;
    private final String bookid;
}
