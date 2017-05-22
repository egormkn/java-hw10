package ru.ifmo.ctddev.titova.rmi.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Titova Sophia
 *         Person in bank.
 */
public interface Person extends Remote {

    /**
     * Get first name of a person.
     *
     * @return first name of person.
     * @throws RemoteException if communication error occurs.
     */
    String getFirstName() throws RemoteException;

    /**
     * Get second name of a person.
     *
     * @return second name of person.
     * @throws RemoteException if communication error occurs.
     */
    String getSecondName() throws RemoteException;

    /**
     * Get passport id of a person.
     *
     * @return passport name of person.
     * @throws RemoteException if communication error occurs.
     */
    String getPassport() throws RemoteException;
}
