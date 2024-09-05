package com.example.bakerymanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ProductService {

    // Ürün ekleme fonksiyonu
    public static void addProduct(String name, double price) {
        String sql = "INSERT INTO products(name, price) VALUES(?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.executeUpdate();
            System.out.println("Ürün başarıyla eklendi: " + name);
        } catch (SQLException e) {
            System.out.println("Ürün eklenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Ürünleri listeleme fonksiyonu (ID'leri de gösterir)
    public static void listAllProducts() {
        String sql = "SELECT * FROM products";
        Set<String> displayedProducts = new HashSet<>(); // Tekrarları engellemek için Set

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Mevcut Ürünler:");
            while (rs.next()) {
                int productId = rs.getInt("id");  // Ürün ID'sini al
                String productName = rs.getString("name");
                double productPrice = rs.getDouble("price");
                if (!displayedProducts.contains(productName)) {
                    System.out.println("Ürün ID: " + productId + " - Ürün: " + productName + ", Fiyat: " + productPrice);
                    displayedProducts.add(productName);
                }
            }

        } catch (SQLException e) {
            System.out.println("Ürünler listelenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Ürüne madde ekleme
    public static void addIngredientToProduct(int productId, int ingredientId) {
        // İlk olarak, bu ilişkinin zaten var olup olmadığını kontrol edelim
        String checkSql = "SELECT COUNT(*) AS count FROM product_ingredients WHERE product_id = ? AND ingredient_id = ?";
        String insertSql = "INSERT INTO product_ingredients(product_id, ingredient_id) VALUES(?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, productId);
            checkStmt.setInt(2, ingredientId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                int count = rs.getInt("count");
                if (count > 0) {
                    System.out.println("Bu madde zaten bu ürüne eklenmiş.");
                    return;
                }
            }

            // İlişki yoksa ekle
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, productId);
                insertStmt.setInt(2, ingredientId);
                insertStmt.executeUpdate();
                System.out.println("Madde ürün ile başarıyla ilişkilendirildi.");
            }

        } catch (SQLException e) {
            System.out.println("Maddeyi ürüne eklerken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

