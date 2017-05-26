package rmi.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Bank interface
 *
 * @author Sophia Titova
 * @author Egor Makarenko
 */
public interface Bank extends Remote {

    /**
     * @param passport id of a person.
     * @return corresponding local person, if exists, <tt>null</tt> otherwise.
     * @throws RemoteException if communication error occurs.
     */
    Person getLocalPerson(String passport) throws RemoteException;

    /**
     * @param passport id of a person.
     * @return corresponding remote person, if exists, <tt>null</tt> otherwise.
     * @throws RemoteException if communication error occurs.
     */
    Person getRemotePerson(String passport) throws RemoteException;

    /**
     * Register new person in bank with given first name, second name and passport id.
     *
     * @param firstName  first name of a person
     * @param lastName   last name of a person
     * @param passport   passport id of a person
     * @return <tt>true</tt> if person was successfully created, <tt>false</tt> otherwise.
     * @throws RemoteException if communication error occurs.
     */
    boolean createPerson(String firstName, String lastName, String passport) throws RemoteException;

    /**
     * Check if person with given first name, last name and passport id is registered in the bank.
     *
     * @param firstName  first name of a person
     * @param lastName   last name of a person
     * @param passport   passport id of a person
     * @return <tt>true</tt> if person exists, <tt>false</tt> otherwise.
     * @throws RemoteException if communication error occurs.
     */
    boolean checkPerson(String firstName, String lastName, String passport) throws RemoteException;
}
