/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eternal.api.resources;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    //private static final String DB_URL = "jdbc:postgresql://host.containers.internal:5432/eternallegacyqrcd /home";//"jdbc:postgresql://localhost:5432/eternallegacyqr";
    
    private static final String DB_URL ="jdbc:postgresql://host.containers.internal:5432/eternallegacyqr";

    private static final String DB_USER ="postgres";

    private static final String DB_PASSWORD ="01Febrero1981;";

    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("========================================");
            System.out.println("PostgreSQL JDBC Driver cargado correctamente");
            System.out.println("========================================");
        } catch (ClassNotFoundException e) {
            System.err.println("========================================");
            System.err.println("ERROR: No se pudo cargar PostgreSQL JDBC Driver");
            System.err.println("========================================");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(
                DB_URL,
                DB_USER,
                DB_PASSWORD
        );
    }
}