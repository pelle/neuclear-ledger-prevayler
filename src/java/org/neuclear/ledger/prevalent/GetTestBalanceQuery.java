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
public class GetTestBalanceQuery implements Query {
    public GetTestBalanceQuery(String ledger) {
        this.ledger = ledger;
    }

    /**
     * @param system        The Prevalent System to be queried.
     * @param executionTime The "current" time.
     * @return The result of this Query.
     * @throws Exception Any Exception encountered by this Query.
     */
    public Object query(Object system, Date executionTime) throws Exception {
        return new Double(((LedgerSystem) system).getBalanceTable().getTestBalance(ledger));
    }

    private final String ledger;

}
