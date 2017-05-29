import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rmi.bank.*;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.Assert.*;


/**
 * Created by roman on 29.05.17.
 */
public class BankTestGolchin {
    private static final int PORT = 8888;
    private static Bank bank;
    private static final int N = 1000;


    @BeforeClass
    public static void beforeClass() throws Exception {
        BankImpl bankImpl = new BankImpl(PORT);
        UnicastRemoteObject.exportObject(bankImpl, PORT);
        Naming.rebind("//localhost/bank", bankImpl);
        bank = (Bank) Naming.lookup("//localhost/bank");
        for (int i = 1; i <= N; i++) {
            bank.createPerson("fn" + i, "ln" + i, "" + i);
            bank.getRemotePerson("" + i).createAccount("0");
        }
    }

    @Test
    public void test1_single_person() throws Exception {
        String passport = "4321";
        assertTrue(bank.createPerson("a", "b", passport));
        assertFalse(bank.checkPerson("a", "b", passport + "1"));
        Person remotePerson = bank.getRemotePerson(passport);
        assertNotNull(remotePerson);
        assertTrue(remotePerson.getAccounts(passport).isEmpty());
        String id = "0";
        Account account = remotePerson.createAccount(id);
        assertEquals(account.getAmount(), 0);
    }

    @Test
    public void test2_exceptions() throws Exception {
        bank.createPerson("a0", "b0", "0");
        String detailMessage = "creating person with already used passport number must return false";
        if (bank.createPerson("a1", "b1", "0"))
            fail(detailMessage);
        detailMessage = "person must not change amount on other's account";
        assertNull(detailMessage, bank.getLocalPerson("0").getAccounts("1"));
    }

    @Test
    public void test3_concurrent() throws Exception {
        int nThreads = 10;
        int nPeople = 100;
        AtomicInteger[] sums = Stream.generate(AtomicInteger::new).limit(N + 10).toArray(AtomicInteger[]::new);
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < nThreads; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < nPeople; j++) {
                    try {
                        int index = random.nextInt();
                        int delta = random.nextInt();
                        sums[index].addAndGet(delta);
                        Person remotePerson = bank.getRemotePerson("" + index);
                        Account account = remotePerson.getAccount("0");
                        account.setAmount(delta);
                    } catch (RemoteException e) {
                        System.err.println("communication error");
                    }
                }
            });
        }
        executorService.shutdownNow();
        for (int i = 1; i <= N; i++) {
            assertEquals(sums[i].get(),
                    bank.getLocalPerson("" + i).getAccount("0").getAmount());
        }
    }

}
