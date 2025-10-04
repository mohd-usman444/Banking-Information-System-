package BankManagment;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type; // Deposit, Withdrawal, Transfer ...
    private double amount;
    private double resultingBalance;
    private String dateTime;

    public Transaction(String type, double amount, double resultingBalance) {
        this.type = type;
        this.amount = amount;
        this.resultingBalance = resultingBalance;
        this.dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public double getResultingBalance() { return resultingBalance; }
    public String getDateTime() { return dateTime; }

    @Override
    public String toString() {
        return String.format("%s | %s | Amount: %.2f | Balance: %.2f", dateTime, type, amount, resultingBalance);
    }
}

