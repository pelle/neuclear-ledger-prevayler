package org.neuclear.ledger.prevalent;

import gnu.trove.TObjectLongHashMap;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Mar 20, 2004
 * Time: 1:47:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionTable implements Serializable {
    private final TObjectLongHashMap transactions=new TObjectLongHashMap();

    boolean exists(String id){
        return transactions.containsKey(id);
    }
    long register(String id,Date time){
        if (!exists(id)) {
            return transactions.put(id,time.getTime());
        }
        return getTransactionTime(id);
    }

    long getTransactionTime(String id) {
        return transactions.get(id);
    }
}
