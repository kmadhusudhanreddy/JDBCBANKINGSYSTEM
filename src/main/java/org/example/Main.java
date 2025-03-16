package org.example;
import org.example.Entity.AccountManager;
import org.example.Entity.Accounts;
import org.example.Entity.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        User user = new User();
        Accounts accounts = new Accounts();
        Scanner sc  =   new Scanner(System.in);

        List<User> users = new ArrayList<>();

        String url = "jdbc:mysql://localhost:3306/jdbc_banking";
        String userName = "root";
        String password = "********";
        int account_number = 0 ;
        try{
            Connection connection = DriverManager.getConnection(url , userName , password);
            AccountManager accountManager = new AccountManager(connection , sc);
            int choice = 10 ;
            int accountNumber = 0 ;
            while (true){
                System.out.println("WELCOME TO BANK OF BHARAT");
                System.out.println("1. REGISTER");
                System.out.println("2. LOGIN");
                System.out.println("3. EXIT");
                System.out.print("ENTER YOUR CHOICE : ");
                choice = sc.nextInt();
                switch (choice){
                    case 1:
                        System.out.print("Enter Email: ");
                        String userEmail = sc.next();
                         boolean registeredExists = accounts.registerExists(connection, userEmail);
                        if (!registeredExists) {
                            user.register(connection, sc , userEmail);
                        } else {
                            System.out.println("User already registered with this email.");
                        }
                        break;

                    case 2:
                       String email = user.userLogin(connection,sc);
                        if(email!=null){
                            System.out.println();
                            System.out.println("User Logged In!");
                            if(!accounts.accountExists(connection ,email)){
                                System.out.println();
                                System.out.println("1. Open a new Bank Account");
                                System.out.println("2. Exit");
                                if(sc.nextInt() == 1) {
                                    account_number = accounts.openAccount(connection , sc , email);

                                }else{
                                    break;
                                }

                            }
                            account_number = accounts.getAccountNumber(connection , sc ,email);
                            int choice2 = 0;
                            while (choice2 != 5) {
                                System.out.println();
                                System.out.println("1. Debit Money");
                                System.out.println("2. Credit Money");
                                System.out.println("3. Transfer Money");
                                System.out.println("4. Check Balance");
                                System.out.println("5. get account number");
                                System.out.println("6. Log Out");
                                System.out.print("Enter your choice: ");
                                choice2 = sc.nextInt();
                                switch (choice2) {
                                    case 1:
                                        accountManager.debitMoney(account_number);
                                        break;
                                    case 2:
                                        accountManager.creditMoney(account_number);
                                        break;
                                    case 3:
                                        accountManager.transferMoney(account_number);
                                        break;
                                    case 4:
                                        accountManager.getBalance(account_number);
                                        break;
                                    case 5:
                                        int accNum = accounts.getAccountNumber(connection, sc, email);
                                        if (accNum != 0) {
                                            System.out.println("Your Account Number: " + accNum);
                                        } else {
                                            System.out.println("Account not found!");
                                        }
                                        break;

                                    case 6:
                                        System.out.println("THANK YOU FOR USING BANK OF BHARAT");
                                        System.out.println("signed out");
                                        return;
                                    default:
                                        System.out.println("Enter Valid Choice!");
                                        break;
                                }
                            }

                        }
                        else{
                            System.out.println("Incorrect Email or Password!");
                        }
                    case 3 :
                        System.out.println("THANK YOU FOR USING BANK OF BHARAT");
                        System.out.println("signed out");

                        return;
                    default:
                        System.out.println("Please enter a valid choice");
                        break;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}