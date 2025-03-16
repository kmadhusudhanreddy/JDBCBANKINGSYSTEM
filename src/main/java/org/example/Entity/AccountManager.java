package org.example.Entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class AccountManager {
    Connection connection ;
    Scanner scanner ;

    public AccountManager(Connection connection, Scanner scanner){
        this.connection = connection ;
        this.scanner = scanner ;
    }

    public void debitMoney(int accountNumber){
        System.out.print("Enter Amount to be debited : ");
        double amount = scanner.nextDouble();
        System.out.print("Enter pin : ");
        int pin = scanner.nextInt();
        try{
            connection.setAutoCommit(false);
            String findAccount = "select balance from account where account_number = ?  and security_pin = ? " ;
            PreparedStatement preparedStatement  = connection.prepareStatement(findAccount);
            preparedStatement.setInt(1,accountNumber);
            preparedStatement.setInt(2,pin);
           ResultSet resultSet =  preparedStatement.executeQuery();
           if(resultSet.next()){
               double currentBalance = resultSet.getDouble("balance");
               if (currentBalance>amount){
                   String updateBalance = "update account set balance = balance - ? where account_number =  ?";
                  PreparedStatement preparedStatement1 = connection.prepareStatement(updateBalance);
                  preparedStatement1.setDouble(1,amount);
                  preparedStatement1.setInt(2,accountNumber);
                 int rowsAffected =  preparedStatement1.executeUpdate();
                 if (rowsAffected>0){
                     System.out.println("amount debited successfully");
                     connection.commit();
                     connection.setAutoCommit(true);
                 }
                 else {
                     System.out.println("Transaction failed");
                     connection.rollback();
                     connection.setAutoCommit(true);
                 }

               }
               else {
                   System.out.println("Insufficient balance");
               }
           }
           else {
               System.out.println("please enter correct pin");
           }


        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


    public void creditMoney(int accountNumber) {
        System.out.print("Enter amount to credit: ");
        double amount = scanner.nextDouble();
        System.out.print("Enter pin: ");
        int pin = scanner.nextInt();

        try {
            connection.setAutoCommit(false);

            // Validate account with account number & security pin
            String creditMoney = "SELECT balance FROM account WHERE account_number = ? AND security_pin = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(creditMoney);
            preparedStatement.setInt(1, accountNumber);
            preparedStatement.setInt(2, pin);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {  // Account exists
                String creditQuery = "UPDATE account SET balance = balance + ? WHERE account_number = ?";
                PreparedStatement preparedStatement1 = connection.prepareStatement(creditQuery);
                preparedStatement1.setDouble(1, amount);
                preparedStatement1.setInt(2, accountNumber);

                int rowsAffected = preparedStatement1.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Amount credited successfully");
                    connection.commit();
                } else {
                    System.out.println("Transaction failed");
                    connection.rollback();
                }
            } else {
                System.out.println("Please enter the correct account number or PIN");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            try {
                connection.rollback();
            } catch (Exception rollbackEx) {
                System.out.println("Rollback failed: " + rollbackEx.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception ex) {
                System.out.println("Failed to reset auto-commit: " + ex.getMessage());
            }
        }
    }


    public void getBalance(int accountNumber){
        System.out.print("Enter pin : ");
        int pin = scanner.nextInt();
        String balanceQuery = "select balance from account where account_number = ? and security_pin = ?";
        try{
           PreparedStatement preparedStatement =  connection.prepareStatement(balanceQuery);
           preparedStatement.setInt(1,accountNumber);
           preparedStatement.setInt(2,pin);
           ResultSet resultSet =  preparedStatement.executeQuery();
           if (resultSet.next()){
               double availbleBalance = resultSet.getDouble("balance");
               System.out.println("the available balance is " + availbleBalance);
           }
           else {
               System.out.println("please enter correct pin");
           }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void transferMoney(int accountNumber) {
        System.out.print("Enter receiver account number: ");
        int receiverAccount = scanner.nextInt();
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();
        System.out.print("Enter PIN: ");
        int pin = scanner.nextInt();

        try {
            connection.setAutoCommit(false); // Start transaction

            // Check sender balance and PIN
            String senderQuery = "SELECT balance FROM account WHERE account_number = ? AND security_pin = ?";
            PreparedStatement senderStmt = connection.prepareStatement(senderQuery);
            senderStmt.setInt(1, accountNumber);
            senderStmt.setInt(2, pin);
            ResultSet senderResult = senderStmt.executeQuery();

            if (!senderResult.next()) {
                System.out.println("Invalid account number or PIN.");
                return;
            }

            double availableBalance = senderResult.getDouble("balance");

            // Check if the receiver account exists
            String receiverQuery = "SELECT * FROM account WHERE account_number = ?";
            PreparedStatement receiverStmt = connection.prepareStatement(receiverQuery);
            receiverStmt.setInt(1, receiverAccount);
            ResultSet receiverResult = receiverStmt.executeQuery();

            if (!receiverResult.next()) {
                System.out.println("Receiver account does not exist.");
                return;
            }

            // Check if the sender has enough balance
            if (availableBalance < amount) {
                System.out.println("Insufficient balance.");
                return;
            }

            // Deduct money from sender
            String deductQuery = "UPDATE account SET balance = balance - ? WHERE account_number = ?";
            PreparedStatement deductStmt = connection.prepareStatement(deductQuery);
            deductStmt.setDouble(1, amount);
            deductStmt.setInt(2, accountNumber);

            // Add money to receiver
            String addQuery = "UPDATE account SET balance = balance + ? WHERE account_number = ?";
            PreparedStatement addStmt = connection.prepareStatement(addQuery);
            addStmt.setDouble(1, amount);
            addStmt.setInt(2, receiverAccount);

            int senderUpdate = deductStmt.executeUpdate();
            int receiverUpdate = addStmt.executeUpdate();

            if (senderUpdate > 0 && receiverUpdate > 0) {
                System.out.println("Amount transferred successfully.");
                connection.commit(); // Commit transaction
            } else {
                System.out.println("Transaction failed.");
                connection.rollback(); // Rollback transaction
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            try {
                connection.rollback(); // Rollback if an error occurs
            } catch (Exception rollbackEx) {
                System.out.println("Rollback failed: " + rollbackEx.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true); // Reset AutoCommit
            } catch (Exception ex) {
                System.out.println("Failed to reset auto-commit: " + ex.getMessage());
            }
        }
    }

}
