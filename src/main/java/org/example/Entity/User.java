package org.example.Entity;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class User {
    String name ;
    String hashPassword ;
    Long mobileNumber ;
    String email ;

    public  User(){

    }

    public User(String name, String password, Long mobileNumber, String email) {
        this.name = name ;
        this.hashPassword = password ;
        this.mobileNumber = mobileNumber ;
        this.email = email ;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return hashPassword;
    }

    public void setPassword(String password) {
        this.hashPassword = password;
    }

    public Long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(Long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void register(Connection connection , Scanner scanner , String email){



        System.out.print("Enter name : ");
        name = scanner.next();
        setName(name);
        System.out.print("Enter Password : ");
        String password = scanner.next();
        String hashPassword = hashedPassword(password);
        setPassword(hashPassword);
        System.out.print("Enter mobile no : ");
        mobileNumber = scanner.nextLong();
        setMobileNumber(mobileNumber);

        setEmail(email);
        String registerUser = "insert into user(name , password , mobile_number , email) values(? , ? ,? , ?)";
        try{
            PreparedStatement preparedStatement =  connection.prepareStatement(registerUser);
            preparedStatement.setString(1 , name);
            preparedStatement.setString(2 , hashPassword);
            preparedStatement.setLong(3 , mobileNumber);
            preparedStatement.setString(4,email);
            int rowsEffected = preparedStatement.executeUpdate();
            if (rowsEffected>0){
                System.out.println("User Register Successfully");

            }
            else {
                System.out.println("User Register Failed");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String hashedPassword(String plainPassword){
        return BCrypt.hashpw(plainPassword , BCrypt.gensalt()) ;
    }

    public boolean checkPassword(String plainPassword){
        return  BCrypt.checkpw(plainPassword , this.getPassword());
    }

    public String userLogin(Connection connection , Scanner scanner ){
        System.out.print("Enter email : ");
        String email = scanner.next();
        System.out.print("Enter password : ");
        String password = scanner.next();

        String loginQuery = "Select password from user where email = ? " ;
        try{
                PreparedStatement preparedStatement = connection.prepareStatement(loginQuery);
                preparedStatement.setString(1,email);
                ResultSet resultSet =  preparedStatement.executeQuery();
                if (resultSet.next()){
                   String hashPass = resultSet.getString("password");
                   if (BCrypt.checkpw(password , hashPass)){
                       System.out.println("user login successfully");
                       return email ;
                   }
                   else {
                       System.out.println("Incorrect password");
                   }
                }
                else {
                    System.out.println("Email not registered");
                }

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return  null;

    }
}
