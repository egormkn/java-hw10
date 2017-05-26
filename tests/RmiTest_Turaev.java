// TODO: Update for the modified interfaces

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import rmi.bank.Account;
import rmi.bank.Person;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * Created by mekhrubon on 22.05.2017.
 */
public class RmiTest {
    private Map<String, Person> myLittleWorld = new HashMap<>();
    private Map<Person, Map<String, Account>> bank = new HashMap<>();
    Random random = new Random(System.nanoTime());



    private void addPerson(Person person) {
        try {
            myLittleWorld.putIfAbsent(person.getPassport(), person);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private void addAccount(Person person, String accountSerial) {
        bank.putIfAbsent(person, new HashMap<>());
        bank.get(person).putIfAbsent(accountSerial, new Account() {
            int amount = 0;
            @Override
            public String getId() throws RemoteException {
                return person.getPassport();
            }

            @Override
            public int getAmount() throws RemoteException {
                return amount;
            }

            @Override
            public void setAmount(int amount) throws RemoteException {
                this.amount = amount;
            }
        });
    }
    private int addMoney(Person person, String accountSerial, int damount) throws RemoteException {
        Account account = bank.get(person).get(accountSerial);
        int amount = account.getAmount() + damount;
        account.setAmount(amount);
        return amount;
    }

    void baseTest(int operationsAmount, int peopleAmount, List<Integer> s) throws Exception {
        List<Person> people = new ArrayList<>();
        List<List<String>> accounts = new ArrayList<>();

        for (int i = 0; i < peopleAmount; ++i) {
            people.add(new Person() {
                String name = RandomStringUtils.randomAlphabetic(random.nextInt(10));
                String surname = RandomStringUtils.randomAlphabetic(random.nextInt(10));
                String id = RandomStringUtils.randomAlphabetic(random.nextInt(10));
                @Override
                public String getSecondName() throws RemoteException {
                    return surname;
                }

                @Override
                public String getPassport() throws RemoteException {
                    return id;
                }

                @Override
                public String getFirstName() throws RemoteException {
                    return name;
                }
            });
            accounts.add(new ArrayList<>());
            addPerson(people.get(i));
            for (int j = 0; j < s.get(i); ++j) {
                accounts.get(i).add(RandomStringUtils.randomAlphabetic(10));
                addAccount(people.get(i), accounts.get(i).get(j));
            }
        }

        Client client = new Client();
        try (Server server = new Server()) {
            server.start(random.nextInt(1000) + 8000);
            for (int operations = 0; operations < operationsAmount; operations++) {
                int personNumber = random.nextInt(peopleAmount);
                int accountNumber = random.nextInt(s.get(personNumber));
                int dx = random.nextInt(2001) - 1000;
                Person person = people.get(personNumber);
                String accountSerial = accounts.get(personNumber).get(accountNumber);

                int expected = addMoney(person, accountSerial, dx);
                assertEquals(expected,
                        client.run(new String[]{person.getFirstName(), person.getSecondName(), person.getPassport(), accountSerial, String.valueOf(dx)}));
            }
        }
    }

    @Test
    public void singlePersonSingleAccount() throws Exception {
        baseTest(100, 1, Collections.singletonList(1));
    }

    @Test
    public void singlePersonMultipleAccounts() throws Exception {
        baseTest(100, 1, Collections.singletonList(random.nextInt(100) + 1));
    }

    @Test
    public void multiplePersonsSingleAccount() throws Exception {
        baseTest(2000, 100, random.ints(100, 1, 2).boxed()
                .collect(Collectors.toList()));
    }

    @Test
    public void multiplePersonsMultipleAccounts() throws Exception {
        baseTest(2000, 100, random.ints(100, 1, 101).boxed()
                .collect(Collectors.toList()));

    }
}