package com.example.bakerymanagementsystem;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static Connection connection;

    // Veritabanına bağlanma
    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            String dbPath = "C:/Users/Acer/Desktop/bakery.db"; // Veritabanı dosya yolu
            File dbFile = new File(dbPath);

            // Veritabanı dosyasını kontrol et ve bağlan
            if (!dbFile.exists()) {
                System.out.println("Veritabanı dosyası mevcut değil, yeni oluşturulacak.");
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

            // Yabancı anahtarların aktif olduğundan emin olalım
            Statement stmt = connection.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON;");

            // Veritabanı bağlantısını başarıyla döndür
            return connection;

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Veritabanına bağlanırken hata oluştu: " + e.getMessage());
            e.printStackTrace();
            return null; // Eğer hata olursa null döndür
        }
    }


    // Tabloları oluştur
    public static void createTables() {
        String createProductTable = "CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "price REAL NOT NULL" +
                ");";

        String createIngredientTable = "CREATE TABLE IF NOT EXISTS ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "sub_ingredient_id INTEGER, " +
                "FOREIGN KEY (sub_ingredient_id) REFERENCES ingredients(id)" +
                ");";

        String createProductIngredientRelationTable = "CREATE TABLE IF NOT EXISTS product_ingredients (" +
                "product_id INTEGER, " +
                "ingredient_id INTEGER, " +
                "PRIMARY KEY (product_id, ingredient_id), " +
                "FOREIGN KEY (product_id) REFERENCES products(id), " +
                "FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)" +
                ");";

        try (Connection conn = DatabaseManager.connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createProductTable);
            stmt.execute(createIngredientTable);
            stmt.execute(createProductIngredientRelationTable);
            System.out.println("Tablolar başarıyla oluşturuldu veya zaten mevcut.");
        } catch (SQLException e) {
            System.out.println("Tablolar oluşturulurken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
