package rmi.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Bank account interface
 *
 * @author Sophia Titova
 * @author Egor Makarenko
 */
public interface Account extends Remote {

    /**
     * Get id of account.
     *
     * @return id of account.
     * @throws RemoteException if communication error occurs.
     */
    String getId() throws RemoteException;

    /**
     * Get amount of money on account.
     *
     * @return amount of money on account.
     * @throws RemoteException if communication error occurs.
     */
    int getAmount() throws RemoteException;

    /**
     * Set amount of money on account.
     *
     * @param amount amount of money to set.
     * @throws RemoteException if communication error occurs.
     */
    void setAmount(int amount) throws RemoteException;
}