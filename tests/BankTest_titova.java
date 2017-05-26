
// TODO: Update for the modified interfaces

import org.junit.BeforeClass;
import org.junit.Test;
import rmi.bank.Account;
import rmi.bank.Bank;
import rmi.bank.BankImpl;
import rmi.bank.Person;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.Set;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;


/**
 * Tests bank application.
 */
public class BankTest {



    private final static int PORT = 8888;
    private static final int PERSONS = 100;
    private static final int ACCOUNTS = 1000;
    private static final String FIRST_NAME = "Sophia";
    private static final String SECOND_NAME = "_is_the_best_programmer!";
    private static Bank bank;

    /**
     * Launches bank and inflates it with some people and accounts.
     *
     * @throws Exception if failed to launch and inflate bank.
     */
    @BeforeClass
    public static void beforeClass() throws Exception {

        Naming.rebind("rmi://localhost/bank1", new BankImpl(PORT));
        bank = (Bank) Naming.lookup("rmi://localhost/bank1");
        Random random = new Random(2539);

        for (int i = 0; i < PERSONS; ++i) {
            assertTrue(bank.createPerson(FIRST_NAME + i, SECOND_NAME, "" + i));
            assertNotNull(bank.createAccount("" + i, "" + (-i - 1)));
        }

        for (int i = 0; i < ACCOUNTS; ++i) {
            Account account;
            assertNotNull(account = bank.createAccount("" + random.nextInt(PERSONS), "" + i));
            account.setAmount(random.nextInt());
        }

        System.out.println("Bank inflated");
    }

    /**
     * Test {@link Bank#getLocalPerson(String)} and {@link Bank#getRemotePerson(String)} methods.
     *
     * @throws RemoteException if communication error occurs.
     */
    @Test
    public void getPersonTest() throws RemoteException {
        assertNull(bank.getLocalPerson("" + (-PERSONS - 1)));
        assertNull(bank.getRemotePerson("" + (-PERSONS - 1)));
        for (int i = 0; i < PERSONS; ++i) {
            Person localPerson = bank.getLocalPerson("" + i);
            assertNotNull(localPerson);
            Person remotePerson = bank.getRemotePerson(localPerson.getPassport());
            assertNotNull(remotePerson);
            assertEquals(localPerson.getFirstName(), remotePerson.getFirstName());
            assertEquals(localPerson.getLastName(), remotePerson.getLastName());
            assertEquals(localPerson.getPassport(), remotePerson.getPassport());
        }
    }

    /**
     * Test {@link Person#getAccounts(String)}.
     *
     * @throws RemoteException if communication error occurs.
     */
    @Test
    public void accountsIdsTest() throws RemoteException {
        for (int i = 0; i < PERSONS; ++i) {
            Set<String> ids = bank.getAccountIds("" + i);
            assertNotNull(ids);
            assertTrue(ids.size() > 0);
        }
    }

    /**
     * Test {@link Bank#createPerson(String, String, String)}.
     *
     * @throws RemoteException if communication error occurs.
     */
    @Test
    public void createPersonTest() throws RemoteException {
        for (int i = 0; i < PERSONS; ++i) {
            assertFalse(bank.createPerson(FIRST_NAME + i, SECOND_NAME, "" + i));
            assertTrue(bank.createPerson(FIRST_NAME + i, SECOND_NAME, "" + (PERSONS + i)));
        }
    }

    /**
     * Test {@link Bank#checkPerson(String, String, String)}}.
     *
     * @throws RemoteException if communication error occurs.
     */
    @Test
    public void checkPersonTest() throws RemoteException {
        for (int i = 0; i < PERSONS; ++i) {
            assertTrue(bank.checkPerson(FIRST_NAME + i, SECOND_NAME, "" + i));
            assertFalse(bank.createPerson(FIRST_NAME + (PERSONS + i), SECOND_NAME, "" + i));
            assertFalse(bank.createPerson(FIRST_NAME + i, SECOND_NAME + (PERSONS + i), "" + i));
            assertTrue(bank.createPerson(FIRST_NAME + i, SECOND_NAME + (PERSONS + i), "" + (i + 2 * PERSONS)));
        }
    }

    /**
     * Test {@link Bank#createAccount(String, String)}.
     *
     * @throws RemoteException if communication error occurs.
     */
    @Test
    public void createAccountTest() throws RemoteException {

        Random random = new Random(PERSONS);
        try {
            bank.createAccount("-1", "0");
            fail("no person associated with passport -1, exception should be thrown");
        } catch (IllegalArgumentException | RemoteException ignored) {
            //ignored...
        } catch (Exception e) {
            fail("IllegalArgumentException exception expected");
        }
        for (int i = 0; i < PERSONS; ++i) {
            assertNull(bank.createAccount("" + i, "" + i));
        }
        for (int i = 0; i < ACCOUNTS; ++i) {
            assertNull(bank.createAccount("0", "" + i));
        }
        for (int i = 0; i < PERSONS; i++) {
            String id = "" + (-ACCOUNTS - ACCOUNTS - i);
            Account account = bank.createAccount("" + i, id);
            assertNotNull(account);
            assertEquals(account.getId(), id);
            assertEquals(account.getAmount(), 0);
        }
    }

    /**
     * Test {@link Bank#getAccount(String)}.
     *
     * @throws RemoteException if communication error occurs.
     */
    @Test
    public void getAccount() throws RemoteException {
        for (int i = 0; i < ACCOUNTS; ++i) {
            Account account = bank.getAccount("" + i);
            assertNotNull(account);
            account.setAmount(-i);
            assertEquals(-i, account.getAmount());
        }
        assertNull(bank.getAccount("" + (-PERSONS - PERSONS - 1)));
    }
}
