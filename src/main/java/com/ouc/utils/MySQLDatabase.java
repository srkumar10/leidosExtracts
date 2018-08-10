package com.ouc.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDatabase {

    //public final static String DB_DRIVER_CLASS = "com.mysql.jdbc.Driver";
    //public final static String DB_URL = "jdbc:mysql://localhost:3306/test";
    //public final static String DB_USERNAME = "root";
    //public final static String DB_PASSWORD = "kumar10";
    //private Logger logger = LogManager.getLogger();
    private Connection dbConnection = null;


    public Connection getDBConnection() {

        ConfigProperties props = new ConfigProperties();

        try {

            // load the Driver Class
            Class.forName(props.readProperty("MYSQL_DB_DRIVER_CLASS"));

            // create the connection now
            dbConnection = DriverManager.getConnection(props.readProperty("MYSQL_DB_URL"), props.readProperty("MYSQL_DB_USERNAME"), props.readProperty("MYSQL_DB_PASSWORD"));
            System.out.println("DB Connection created successfully");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            //logger.log(Level.WARN, e.getMessage(), e);
        }

        return dbConnection;
    }

    public void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
                //logger.log(Level.WARN, e.getMessage(), e);
            }
        }
    }
}
