package org.neuclear.ledger.prevalent;

import org.neuclear.ledger.LedgerTest;
import org.neuclear.ledger.Ledger;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.UnknownLedgerException;
import org.neuclear.commons.NeuClearException;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Mar 20, 2004
 * Time: 1:05:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrevalentLedgerTest extends LedgerTest {
    public PrevalentLedgerTest(String s) throws LowlevelLedgerException, UnknownLedgerException, SQLException, NamingException, IOException, NeuClearException {
        super(s);
    }

    public Ledger createLedger() throws LowlevelLedgerException {
        try {
            return new PrevalentLedger("test","target/test-data/ledger/");
        } catch (IOException e) {
            throw new LowlevelLedgerException(e);
        } catch (ClassNotFoundException e) {
            throw new LowlevelLedgerException(e);
        }
    }
}
