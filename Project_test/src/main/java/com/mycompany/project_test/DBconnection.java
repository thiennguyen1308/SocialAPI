package com.mycompany.project_test;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author root
 */
public class DBconnection {

    private static final String SERVER_HOST = "192.168.33.10:3306";
    private static final String TEST_API = "TEST_API";
    private static final String USERNAME = "mieruca";
    private static final String PASSWORD = "faber@2016";
    
    //<editor-fold defaultstate="collapsed" desc="GET CONNECTION">
    public static Connection getConnection() {
        Connection connect = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connect = DriverManager.getConnection("jdbc:mysql://" + SERVER_HOST + "/" + TEST_API + "?" + "user=" + USERNAME + "&password=" + PASSWORD);
        } catch (Exception e) {
        }
        return connect;
    }
    //</editor-fold>
}
