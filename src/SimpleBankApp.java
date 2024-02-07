import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

class Bank {
    private Map<String, Double> accounts;
    private Map<String, Double> loans;
    private Map<String, List<String>> transactionHistory;

    public Bank() {
        accounts = new HashMap<>();
        loans = new HashMap<>();
        transactionHistory = new HashMap<>();
    }

    public double getBalance(String accountNumber) {
        return accounts.getOrDefault(accountNumber, 0.0);
    }

    public double getLoanBalance(String accountNumber) {
        return loans.getOrDefault(accountNumber, 0.0);
    }

    public void createAccount(String accountNumber, double initialBalance) {
        accounts.put(accountNumber, initialBalance);
        loans.put(accountNumber, 0.0); // Initialize loans to 0.0 for new accounts
        transactionHistory.put(accountNumber, new ArrayList<>());
    }

    public void deposit(String accountNumber, double amount) {
        double currentBalance = accounts.getOrDefault(accountNumber, 0.0);
        accounts.put(accountNumber, currentBalance + amount);
        addToTransactionHistory(accountNumber, "Deposit: +" + amount);
    }

    public void withdraw(String accountNumber, double amount) {
        double currentBalance = accounts.getOrDefault(accountNumber, 0.0);
        if (currentBalance >= amount) {
            accounts.put(accountNumber, currentBalance - amount);
            addToTransactionHistory(accountNumber, "Withdrawal: -" + amount);
        } else {
            JOptionPane.showMessageDialog(null, "Insufficient funds", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void takeLoan(String accountNumber, double amount) {
        double currentLoan = loans.getOrDefault(accountNumber, 0.0);
        loans.put(accountNumber, currentLoan + amount);
        deposit(accountNumber, amount); // Deposit the loan amount into the account
        addToTransactionHistory(accountNumber, "Loan: +" + amount);
    }

    public void transfer(String fromAccount, String toAccount, double amount) {
        double fromBalance = accounts.getOrDefault(fromAccount, 0.0);
        double toBalance = accounts.getOrDefault(toAccount, 0.0);

        if (fromBalance >= amount) {
            withdraw(fromAccount, amount);
            deposit(toAccount, amount);
            addToTransactionHistory(fromAccount, "Transfer to " + toAccount + ": -" + amount);
            addToTransactionHistory(toAccount, "Transfer from " + fromAccount + ": +" + amount);
            JOptionPane.showMessageDialog(null, "Transfer successful", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Insufficient funds for transfer", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<String> getTransactionHistory(String accountNumber) {
        return transactionHistory.getOrDefault(accountNumber, Collections.emptyList());
    }

    private void addToTransactionHistory(String accountNumber, String transaction) {
        List<String> history = transactionHistory.getOrDefault(accountNumber, new ArrayList<>());
        history.add(transaction);
        transactionHistory.put(accountNumber, history);
    }
}
class BankGUI extends JFrame {
    private Bank bank;
    private JTextField accountField, amountField, resultField, balanceField;

    public BankGUI(Bank bank) {
        this.bank = bank;
        setTitle("Simple Bank GUI");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createComponents();
        updateBalance(); // Display balance on login
        setVisible(true);
    }

    private void createComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));

        JLabel accountLabel = new JLabel("Account Number:");
        accountField = new JTextField();
        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField();

        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performTransaction("deposit");
            }
        });

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performTransaction("withdraw");
            }
        });

        JButton loanButton = new JButton("Take Loan");
        loanButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performTransaction("loan");
            }
        });

        JButton transferButton = new JButton("Transfer");
        transferButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performTransaction("transfer");
            }
        });

        JButton historyButton = new JButton("Transaction History");
        historyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showTransactionHistory();
            }
        });

        JLabel balanceLabel = new JLabel("Balance:");
        balanceField = new JTextField();
        balanceField.setEditable(false);

        JLabel resultLabel = new JLabel("Result:");
        resultField = new JTextField();
        resultField.setEditable(false);

        panel.add(accountLabel);
        panel.add(accountField);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(depositButton);
        panel.add(withdrawButton);
        panel.add(loanButton);
        panel.add(transferButton);
        panel.add(historyButton);
        panel.add(balanceLabel);
        panel.add(balanceField);
        panel.add(resultLabel);
        panel.add(resultField);

        add(panel);
    }

    private void updateBalance() {
        String accountNumber = accountField.getText();
        double balance = bank.getBalance(accountNumber);
        balanceField.setText(" $" + balance);
    }

    private void performTransaction(String transactionType) {
        String accountNumber = accountField.getText();
        double amount = Double.parseDouble(amountField.getText());

        switch (transactionType) {
            case "deposit":
                bank.deposit(accountNumber, amount);
                break;
            case "withdraw":
                bank.withdraw(accountNumber, amount);
                break;
            case "loan":
                bank.takeLoan(accountNumber, amount);
                break;
            case "transfer":
                String toAccount = JOptionPane.showInputDialog("Enter the recipient's account number:");
                bank.transfer(accountNumber, toAccount, amount);
                break;
        }

        updateBalance(); // Update balance after each transaction

        double loanBalance = bank.getLoanBalance(accountNumber);
        resultField.setText(transactionType + " successful. Loan Balance: $" + loanBalance);
    }

    private void showTransactionHistory() {
        String accountNumber = accountField.getText();
        List<String> history = bank.getTransactionHistory(accountNumber);

        StringBuilder historyText = new StringBuilder("Transaction History:\n");
        for (String transaction : history) {
            historyText.append(transaction).append("\n");
        }

        JOptionPane.showMessageDialog(this, historyText.toString(), "Transaction History", JOptionPane.INFORMATION_MESSAGE);
    }
}


public class SimpleBankApp {
    public static void main(String[] args) {
        Bank bank = new Bank();
        bank.createAccount("12345678", 200000);

        SwingUtilities.invokeLater(() -> new BankGUI(bank));
    }
}
