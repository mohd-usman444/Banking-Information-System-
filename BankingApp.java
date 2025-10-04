package BankManagment;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.stream.Collectors;

public class BankingApp {
    private Bank bank;
    private JFrame frame;
    private Account currentAccount;

    public BankingApp() {
        bank = Bank.load();
        buildUI();
    }

    private void buildUI() {
        frame = new JFrame("Banking Information System - Prototype");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Register", buildRegisterPanel());
        tabs.add("Login", buildLoginPanel());
        tabs.add("Account", buildAccountPanel());
        tabs.add("Transfer", buildTransferPanel());
        tabs.add("Statements", buildStatementPanel());
        tabs.add("Admin", buildAdminPanel());

        frame.getContentPane().add(tabs);
        frame.setVisible(true);
    }

    // Register panel
    private JPanel buildRegisterPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(10,10,10,10));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.EAST;
        p.add(new JLabel("Full Name:"), c);
        c.gridy++; p.add(new JLabel("Address:"), c);
        c.gridy++; p.add(new JLabel("Contact:"), c);
        c.gridy++; p.add(new JLabel("Password:"), c);
        c.gridy++; p.add(new JLabel("Initial Deposit:"), c);

        JTextField tfName = new JTextField(20);
        JTextField tfAddress = new JTextField(20);
        JTextField tfContact = new JTextField(20);
        JPasswordField pfPassword = new JPasswordField(20);
        JTextField tfDeposit = new JTextField(10);

        c.gridx=1; c.gridy=0; c.anchor = GridBagConstraints.WEST;
        p.add(tfName, c);
        c.gridy++; p.add(tfAddress, c);
        c.gridy++; p.add(tfContact, c);
        c.gridy++; p.add(pfPassword, c);
        c.gridy++; p.add(tfDeposit, c);

        JButton btnRegister = new JButton("Register");
        c.gridy++; c.gridx=1;
        p.add(btnRegister, c);

        btnRegister.addActionListener(e -> {
            try {
                String name = tfName.getText().trim();
                String addr = tfAddress.getText().trim();
                String contact = tfContact.getText().trim();
                String pwd = new String(pfPassword.getPassword());
                double dep = Double.parseDouble(tfDeposit.getText().trim());

                if (name.isEmpty() || pwd.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Name and Password required.");
                    return;
                }

                Account acc = bank.register(name, addr, contact, pwd, dep);
                JOptionPane.showMessageDialog(frame, "Registered successfully. Your account number: " + acc.getAccountNumber());
                tfName.setText(""); tfAddress.setText(""); tfContact.setText(""); pfPassword.setText(""); tfDeposit.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid deposit amount.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        return p;
    }

    // Login panel
    private JPanel buildLoginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(10,10,10,10));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.gridx=0; c.gridy=0; c.anchor = GridBagConstraints.EAST;
        p.add(new JLabel("Account Number:"), c);
        c.gridy++; p.add(new JLabel("Password:"), c);
        JTextField tfAcc = new JTextField(12);
        JPasswordField pf = new JPasswordField(12);
        c.gridx=1; c.gridy=0; c.anchor = GridBagConstraints.WEST;
        p.add(tfAcc, c);
        c.gridy++; p.add(pf, c);

        JButton btnLogin = new JButton("Login");
        JButton btnLogout = new JButton("Logout");
        c.gridy++; p.add(btnLogin, c);
        c.gridx=2; p.add(btnLogout, c);

        btnLogin.addActionListener(e -> {
            try {
                int accNo = Integer.parseInt(tfAcc.getText().trim());
                String pwd = new String(pf.getPassword());
                Account acc = bank.login(accNo, pwd);
                if (acc == null) {
                    JOptionPane.showMessageDialog(frame, "Login failed: invalid credentials.");
                } else {
                    currentAccount = acc;
                    JOptionPane.showMessageDialog(frame, "Login successful. Welcome " + acc.getName());
                    refreshAccountTab();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid account number.");
            }
        });

        btnLogout.addActionListener(e -> {
            currentAccount = null;
            JOptionPane.showMessageDialog(frame, "Logged out.");
            refreshAccountTab();
        });

        return p;
    }

    // Account management panel
    private JPanel accountPanel;
    private JLabel lblAccountInfo;
    private JTextField tfUpdateName, tfUpdateAddress, tfUpdateContact;
    private JPanel buildAccountPanel() {
        accountPanel = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblAccountInfo = new JLabel("Not logged in.");
        top.add(lblAccountInfo);
        accountPanel.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBorder(new EmptyBorder(10,10,10,10));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.gridx=0; c.gridy=0; c.anchor = GridBagConstraints.EAST;
        center.add(new JLabel("Name:"), c);
        c.gridy++; center.add(new JLabel("Address:"), c);
        c.gridy++; center.add(new JLabel("Contact:"), c);
        c.gridy++; center.add(new JLabel("Change Password:"), c);

        tfUpdateName = new JTextField(20);
        tfUpdateAddress = new JTextField(20);
        tfUpdateContact = new JTextField(20);
        JPasswordField pfNewPass = new JPasswordField(12);

        c.gridx=1; c.gridy=0; c.anchor = GridBagConstraints.WEST;
        center.add(tfUpdateName, c);
        c.gridy++; center.add(tfUpdateAddress, c);
        c.gridy++; center.add(tfUpdateContact, c);
        c.gridy++; center.add(pfNewPass, c);

        JButton btnUpdate = new JButton("Update Info");
        JButton btnChangePass = new JButton("Change Password");
        JButton btnDeposit = new JButton("Deposit");
        JButton btnWithdraw = new JButton("Withdraw");

        c.gridy++; center.add(btnUpdate, c);
        c.gridx=2; center.add(btnDeposit, c);
        c.gridx=3; center.add(btnWithdraw, c);
        c.gridx=1; c.gridy++; center.add(btnChangePass, c);

        btnUpdate.addActionListener(e -> {
            if (currentAccount == null) { JOptionPane.showMessageDialog(frame, "Login first."); return; }
            currentAccount.setName(tfUpdateName.getText().trim());
            currentAccount.setAddress(tfUpdateAddress.getText().trim());
            currentAccount.setContact(tfUpdateContact.getText().trim());
            bank.save();
            JOptionPane.showMessageDialog(frame, "Account info updated.");
            refreshAccountTab();
        });

        btnChangePass.addActionListener(e -> {
            if (currentAccount == null) { JOptionPane.showMessageDialog(frame, "Login first."); return; }
            String newp = new String(pfNewPass.getPassword());
            if (newp.isEmpty()) { JOptionPane.showMessageDialog(frame, "Enter new password."); return; }
            currentAccount.changePassword(newp);
            bank.save();
            JOptionPane.showMessageDialog(frame, "Password changed.");
            pfNewPass.setText("");
        });

        btnDeposit.addActionListener(e -> {
            if (currentAccount == null) { JOptionPane.showMessageDialog(frame, "Login first."); return; }
            String s = JOptionPane.showInputDialog(frame, "Enter deposit amount:");
            if (s == null) return;
            try {
                double amt = Double.parseDouble(s);
                bank.deposit(currentAccount.getAccountNumber(), amt);
                JOptionPane.showMessageDialog(frame, "Deposit successful.");
                refreshAccountTab();
            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(frame, "Invalid amount."); }
            catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage()); }
        });

        btnWithdraw.addActionListener(e -> {
            if (currentAccount == null) { JOptionPane.showMessageDialog(frame, "Login first."); return; }
            String s = JOptionPane.showInputDialog(frame, "Enter withdrawal amount:");
            if (s == null) return;
            try {
                double amt = Double.parseDouble(s);
                bank.withdraw(currentAccount.getAccountNumber(), amt);
                JOptionPane.showMessageDialog(frame, "Withdrawal successful.");
                refreshAccountTab();
            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(frame, "Invalid amount."); }
            catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage()); }
        });

        accountPanel.add(center, BorderLayout.CENTER);
        refreshAccountTab();
        return accountPanel;
    }

    private void refreshAccountTab() {
        if (currentAccount == null) {
            lblAccountInfo.setText("Not logged in.");
            tfUpdateName.setText("");
            tfUpdateAddress.setText("");
            tfUpdateContact.setText("");
        } else {
            lblAccountInfo.setText(currentAccount.toString());
            tfUpdateName.setText(currentAccount.getName());
            tfUpdateAddress.setText(currentAccount.getAddress());
            tfUpdateContact.setText(currentAccount.getContact());
        }
    }

    // Transfer panel
    private JPanel buildTransferPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(10,10,10,10));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.gridx=0; c.gridy=0; c.anchor = GridBagConstraints.EAST;
        p.add(new JLabel("Sender Account #:"), c);
        c.gridy++; p.add(new JLabel("Password:"), c);
        c.gridy++; p.add(new JLabel("Recipient Account #:"), c);
        c.gridy++; p.add(new JLabel("Amount:"), c);

        JTextField tfSender = new JTextField(12);
        JPasswordField pfSender = new JPasswordField(12);
        JTextField tfRecipient = new JTextField(12);
        JTextField tfAmount = new JTextField(10);

        c.gridx=1; c.gridy=0; c.anchor = GridBagConstraints.WEST;
        p.add(tfSender, c);
        c.gridy++; p.add(pfSender, c);
        c.gridy++; p.add(tfRecipient, c);
        c.gridy++; p.add(tfAmount, c);

        JButton btnTransfer = new JButton("Transfer");
        c.gridy++; p.add(btnTransfer, c);

        btnTransfer.addActionListener(e -> {
            try {
                int sender = Integer.parseInt(tfSender.getText().trim());
                String pwd = new String(pfSender.getPassword());
                int recipient = Integer.parseInt(tfRecipient.getText().trim());
                double amt = Double.parseDouble(tfAmount.getText().trim());

                Account sAcc = bank.login(sender, pwd);
                if (sAcc == null) { JOptionPane.showMessageDialog(frame, "Invalid sender credentials."); return; }
                bank.transfer(sender, recipient, amt);
                JOptionPane.showMessageDialog(frame, "Transfer successful.");
            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(frame, "Invalid numbers entered."); }
            catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage()); }
        });

        return p;
    }

    // Statements panel
    private JTextArea taStatements;
    private JPanel buildStatementPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(10,10,10,10));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Enter Account # to view statement:"));
        JTextField tfAcc = new JTextField(10);
        JButton btnView = new JButton("View Statement");
        top.add(tfAcc); top.add(btnView);
        p.add(top, BorderLayout.NORTH);

        taStatements = new JTextArea();
        taStatements.setEditable(false);
        JScrollPane sp = new JScrollPane(taStatements);
        p.add(sp, BorderLayout.CENTER);

        btnView.addActionListener(e -> {
            try {
                int acc = Integer.parseInt(tfAcc.getText().trim());
                Account a = bank.getAccount(acc);
                if (a == null) { JOptionPane.showMessageDialog(frame, "Account not found."); return; }
                StringBuilder sb = new StringBuilder();
                sb.append("Statement for Account #").append(acc).append(" - ").append(a.getName()).append("\n");
                sb.append("Current Balance: ").append(String.format("%.2f", a.getBalance())).append("\n\n");
                for (Transaction t : a.getTransactions()) {
                    sb.append(t.toString()).append("\n");
                }
                taStatements.setText(sb.toString());
            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(frame, "Invalid account number."); }
        });

        return p;
    }

    // Admin panel - simple list of accounts (for demo)
    private JPanel buildAdminPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(10,10,10,10));
        JButton btnRefresh = new JButton("Refresh Accounts List");
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        p.add(btnRefresh, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        btnRefresh.addActionListener(e -> {
            String all = bank.listAllAccounts().stream()
                    .map(Account::toString)
                    .collect(Collectors.joining("\n"));
            ta.setText(all);
        });
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankingApp());
    }
}

 
