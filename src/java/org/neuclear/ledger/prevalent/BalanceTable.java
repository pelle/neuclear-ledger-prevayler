package org.neuclear.ledger.prevalent;

import gnu.trove.TObjectDoubleHashMap;

import java.io.Serializable;

/**
 * This contains the balances of all the accounts
 */
public final class BalanceTable implements Serializable{
    private final TObjectDoubleHashMap balances=new TObjectDoubleHashMap();

    double getBalance(final String id){
        if (!balances.containsKey(id))
            return 0;
        return balances.get(id);
    }

    double add(final String id,final double amount){
        if (!balances.containsKey(id))
            return balances.put(id,amount);
        final double nb=balances.get(id)+amount;
        return balances.put(id,nb);
    }
}
