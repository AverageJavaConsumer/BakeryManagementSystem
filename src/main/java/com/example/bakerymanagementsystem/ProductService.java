package com.example.bakerymanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductService {

    // Ürün ekleme fonksiyonu
    public static void addProduct(String name, double price) {
        String sql = "INSERT INTO products(name, price) VALUES(?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ürün eklenirken hata oluştu: " + e.getMessage());
        }
    }

    // Ürünleri listeleme fonksiyonu
    public static void listAllProducts() {
        String sql = "SELECT * FROM products";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Tüm ürünler:");
            while (rs.next()) {
                System.out.println("Ürün: " + rs.getString("name") + ", Fiyat: " + rs.getDouble("price"));
            }

        } catch (SQLException e) {
            System.out.println("Ürünler listelenirken hata oluştu: " + e.getMessage());
        }
    }
}
