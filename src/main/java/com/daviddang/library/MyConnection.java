package com.daviddang.library;

import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

public class MyConnection {

    public static Connection getConnection(){

        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/library", "username", "password");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return con;
    }
}
