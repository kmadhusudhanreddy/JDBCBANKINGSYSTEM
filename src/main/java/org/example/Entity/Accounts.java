package org.example.Entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;

public class Accounts {



    public int openAccount(Connection connection , Scanner scanner , String email){

        String openAccount = "insert into account ( full_name , email , account_number , balance , security_pin) values ( ?,?,?,?,?)";
        System.out.print("Enter fullName : ");
        String name = scanner.next();
        int accountNumber = generateAccountNumber(5);
        System.out.print("Add balance : ");
        double balance = scanner.nextDouble();
        System.out.print("Enter security pin : ");
        int pin = scanner.nextInt();
        try{
           PreparedStatement preparedStatement =  connection.prepareStatement(openAccount);
           preparedStatement.setString(1,name);
           preparedStatement.setString(2,email);
           preparedStatement.setInt(3 , accountNumber);
           preparedStatement.setDouble(4,balance);
           preparedStatement.setInt(5,pin);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Account opened successfully. Account Number: " + accountNumber);
                return accountNumber;
            } else {
                System.out.println("Account opening failed.");
            }


        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        return accountNumber;
    }

    public int generateAccountNumber(int length){
        int accountNum =  0 ;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0 ; i < length ; i++){
            int digit = (int)( Math.random() * 10) ;
            accountNum = accountNum * 10 + digit ;
        }
        return accountNum;
    }

    public int getAccountNumber(Connection connection , Scanner sc , String email){
        String getAccountNumber = "select account_number from account where  email = ?" ;
        try{
          PreparedStatement preparedStatement =   connection.prepareStatement(getAccountNumber);
          preparedStatement.setString(1,email);
         ResultSet resultSet =  preparedStatement.executeQuery();
         if (resultSet.next()){
             return resultSet.getInt("account_number");
         }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return 0;
    }



    public boolean accountExists(Connection connection, String email) {
        String query = "SELECT * FROM account WHERE email = ? ";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true ;  // Returns true if count > 0
            }
        } catch (SQLException e) {
            System.out.println("Error checking account existence: " + e.getMessage());
        }
        return false;
    }

    public boolean registerExists(Connection connection , String email){
        String registerQuery = "select * from user where email = ? ";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(registerQuery);
            preparedStatement.setString(1,email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return true ;
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return false;
    }
}
