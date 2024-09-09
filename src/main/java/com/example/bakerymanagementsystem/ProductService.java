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
        // Önce ürün ve madde veritabanında var mı kontrol edelim
        if (!doesProductExist(productId) || !doesIngredientExist(ingredientId)) {
            System.out.println("Ürün veya madde veritabanında mevcut değil, ekleme yapılamaz.");
            return;
        }

        String insertSql = "INSERT INTO product_ingredients(product_id, ingredient_id) VALUES(?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setInt(1, productId);
            insertStmt.setInt(2, ingredientId);
            insertStmt.executeUpdate();
            System.out.println("Madde ürün ile başarıyla ilişkilendirildi.");
        } catch (SQLException e) {
            System.out.println("Maddeyi ürüne eklerken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Ürünün var olup olmadığını kontrol eden fonksiyon
    private static boolean doesProductExist(int productId) {
        String sql = "SELECT COUNT(*) AS count FROM products WHERE id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Ürün kontrol edilirken hata oluştu: " + e.getMessage());
            return false;
        }
    }

    // Maddenin var olup olmadığını kontrol eden fonksiyon
    private static boolean doesIngredientExist(int ingredientId) {
        String sql = "SELECT COUNT(*) AS count FROM ingredients WHERE id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ingredientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Madde kontrol edilirken hata oluştu: " + e.getMessage());
            return false;
        }
    }

}

