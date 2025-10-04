package BankManagment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private int accountNumber;
    private String name;
    private String address;
    private String contact;
    private String passwordHash; // simple hashed password (not cryptographically strong)
    private double balance;
    private List<Transaction> transactions;

    public Account(int accountNumber, String name, String address, String contact, String password, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.passwordHash = Integer.toString(password.hashCode());
        this.balance = initialDeposit;
        this.transactions = new ArrayList<>();
        transactions.add(new Transaction("Initial Deposit", initialDeposit, balance));
    }

    public int getAccountNumber() { return accountNumber; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getContact() { return contact; }
    public double getBalance() { return balance; }
    public List<Transaction> getTransactions() { return transactions; }

    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setContact(String contact) { this.contact = contact; }

    public boolean checkPassword(String password) {
        return passwordHash.equals(Integer.toString(password.hashCode()));
    }

    public void changePassword(String newPassword) {
        this.passwordHash = Integer.toString(newPassword.hashCode());
    }

    public void deposit(double amount) {
        balance += amount;
        transactions.add(new Transaction("Deposit", amount, balance));
    }

    public void withdraw(double amount) throws IllegalArgumentException {
        if (amount > balance) throw new IllegalArgumentException("Insufficient funds.");
        balance -= amount;
        transactions.add(new Transaction("Withdrawal", amount, balance));
    }

    public void receiveTransfer(double amount, int fromAccount) {
        balance += amount;
        transactions.add(new Transaction("Transfer Received from " + fromAccount, amount, balance));
    }

    public void sendTransfer(double amount, int toAccount) throws IllegalArgumentException {
        if (amount > balance) throw new IllegalArgumentException("Insufficient funds.");
        balance -= amount;
        transactions.add(new Transaction("Transfer Sent to " + toAccount, amount, balance));
    }

    @Override
    public String toString() {
        return "Account#" + accountNumber + " | " + name + " | Balance: " + String.format("%.2f", balance);
    }
}
