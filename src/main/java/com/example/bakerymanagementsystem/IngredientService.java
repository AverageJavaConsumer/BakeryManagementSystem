package com.example.bakerymanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IngredientService {

    // Madde ekleme fonksiyonu
    public static void addIngredient(String name, Integer subIngredientId) {
        String sql = "INSERT INTO ingredients(name, sub_ingredient_id) VALUES(?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            if (subIngredientId != null) {
                pstmt.setInt(2, subIngredientId);
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Madde eklenirken hata oluştu: " + e.getMessage());
        }
    }

    // Maddeleri listeleme fonksiyonu
    public static void listIngredientsForProduct(int productId) {
        String sql = "SELECT i.name FROM ingredients i " +
                "INNER JOIN product_ingredients pi ON i.id = pi.ingredient_id " +
                "WHERE pi.product_id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("Ürün için gereken maddeler:");
                while (rs.next()) {
                    System.out.println("- " + rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Maddeler listelenirken hata oluştu: " + e.getMessage());
        }
    }

    // Alt maddeleri isteğe bağlı olarak listeleme
    public static void listSubIngredients(int ingredientId) {
        String sql = "SELECT name FROM ingredients WHERE sub_ingredient_id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ingredientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("Alt maddeler:");
                while (rs.next()) {
                    System.out.println("- " + rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Alt maddeler listelenirken hata oluştu: " + e.getMessage());
        }
    }
}
