package rmi.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

/**
 * Bank person interface
 *
 * @author Sophia Titova
 * @author Egor Makarenko
 */
public interface Person extends Remote {

    /**
     * Get first name of a person.
     *
     * @return first name of a person.
     * @throws RemoteException if communication error occurs.
     */
    String getFirstName() throws RemoteException;

    /**
     * Get last name of a person.
     *
     * @return last name of a person.
     * @throws RemoteException if communication error occurs.
     */
    String getLastName() throws RemoteException;

    /**
     * Get passport id of a person.
     *
     * @return passport id of a person.
     * @throws RemoteException if communication error occurs.
     */
    String getPassport() throws RemoteException;

    /**
     * Create new empty account and associate it with a person.
     *
     * @param id account id.
     * @return created account, or <tt>null</tt> if account with the same id already exists.
     * @throws RemoteException if communication error occurs.
     */
    Account createAccount(String id) throws RemoteException;

    /**
     * @param id account id.
     * @return account if it exists in the bank, <tt>null</tt> otherwise.
     * @throws RemoteException if communication error occurs.
     */
    Account getAccount(String id) throws RemoteException;

    /**
     * Get a collection of account ids registered for a person.
     *
     * @param passport id of the person.
     * @return collection of accounts registered for the person, if person exists, null otherwise.
     * @throws RemoteException if communication error occurs.
     */
    Collection<String> getAccounts(String passport) throws RemoteException;

}
