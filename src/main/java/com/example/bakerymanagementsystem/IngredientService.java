package com.example.bakerymanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;


public class IngredientService {

    // Madde ekleme fonksiyonu (alt maddeler opsiyonel, fiyatla birlikte)
    public static void addIngredient(String name, double price, Integer subIngredientId) {
        String sql = "INSERT INTO ingredients(name, price, sub_ingredient_id) VALUES(?, ?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);  // Fiyatı ekliyoruz
            if (subIngredientId != null) {
                pstmt.setInt(3, subIngredientId);
            } else {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            }
            pstmt.executeUpdate();
            System.out.println("Madde başarıyla eklendi: " + name);
        } catch (SQLException e) {
            System.out.println("Madde eklenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Ürün için gerekli maddeleri listeleme
    public static void listIngredientsForProduct(int productId) {
        String sql = "SELECT i.id, i.name, i.price FROM ingredients i " +
                "INNER JOIN product_ingredients pi ON i.id = pi.ingredient_id " +
                "WHERE pi.product_id = ?";

        Set<Integer> seenIngredients = new HashSet<>();

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("Ürün için gerekli maddeler:");
                while (rs.next()) {
                    int id = rs.getInt("id");
                    if (seenIngredients.contains(id)) {
                        continue; // Tekrar eden maddeyi atla
                    }
                    seenIngredients.add(id);
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    System.out.println("ID: " + id + " - " + name + " (Fiyat: " + price + ")");
                }
            }
        } catch (SQLException e) {
            System.out.println("Maddeler listelenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Alt maddeleri listeleme
    public static void listSubIngredients(int ingredientId) {
        String sql = "SELECT id, name, price FROM ingredients WHERE sub_ingredient_id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ingredientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("Alt maddeler:");
                boolean hasSubIngredients = false;
                while (rs.next()) {
                    hasSubIngredients = true;
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    System.out.println("ID: " + id + " - " + name + " (Fiyat: " + price + ")");
                }
                if (!hasSubIngredients) {
                    System.out.println("Bu maddenin alt maddesi bulunmamaktadır.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Alt maddeler listelenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
