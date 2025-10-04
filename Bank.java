package BankManagment;

import java.io.*;
import java.util.*;

public class Bank implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<Integer, Account> accounts;
    private int nextAccountNumber;
    private static final String DATA_FILE = "bank.dat";

    public Bank() {
        accounts = new HashMap<>();
        nextAccountNumber = 100100; // starting account number
    }

    public synchronized Account register(String name, String address, String contact, String password, double initialDeposit) {
        int accNo = nextAccountNumber++;
        Account acc = new Account(accNo, name, address, contact, password, initialDeposit);
        accounts.put(accNo, acc);
        save(); // persist after change
        return acc;
    }

    public Account login(int accountNumber, String password) {
        Account acc = accounts.get(accountNumber);
        if (acc != null && acc.checkPassword(password)) return acc;
        return null;
    }

    public Account getAccount(int accNo) { return accounts.get(accNo); }

    public List<Account> listAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public void deposit(int accNo, double amount) {
        Account acc = accounts.get(accNo);
        if (acc == null) throw new IllegalArgumentException("Account not found.");
        acc.deposit(amount);
        save();
    }

    public void withdraw(int accNo, double amount) {
        Account acc = accounts.get(accNo);
        if (acc == null) throw new IllegalArgumentException("Account not found.");
        acc.withdraw(amount);
        save();
    }

    public void transfer(int fromAcc, int toAcc, double amount) {
        Account aFrom = accounts.get(fromAcc);
        Account aTo = accounts.get(toAcc);
        if (aFrom == null) throw new IllegalArgumentException("Sender account not found.");
        if (aTo == null) throw new IllegalArgumentException("Recipient account not found.");
        aFrom.sendTransfer(amount, toAcc);
        aTo.receiveTransfer(amount, fromAcc);
        save();
    }

    // persistence
    public final void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Error saving bank data: " + e.getMessage());
        }
    }

    public static Bank load() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return new Bank();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (obj instanceof Bank) return (Bank) obj;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed loading bank data, starting fresh. (" + e.getMessage() + ")");
        }
        return new Bank();
    }
}

