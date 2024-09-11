package com.example.bakerymanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemService {

    // Ürün veya madde ekleme fonksiyonu
    public static void addItem(String name, double price) {
        String sql = "INSERT INTO items(name, price) VALUES(?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.executeUpdate();
            System.out.println("Ürün veya madde başarıyla eklendi: " + name);

            // Eklendikten sonra tüm ürünleri yazdır (doğrulama amacıyla)
            listAllItems(); // Veritabanındaki tüm ürünleri konsola yazdır
        } catch (SQLException e) {
            System.out.println("Ürün veya madde eklenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Ürüne veya maddeye başka bir madde (ingredient) ekleme
    public static void addIngredientToItem(int itemId, int ingredientId) {
        System.out.println("Kontrol Edilen Ürün ID: " + itemId);
        System.out.println("Kontrol Edilen Madde ID: " + ingredientId);

        if (!doesItemExist(itemId) || !doesItemExist(ingredientId)) {
            System.out.println("Ürün veya madde veritabanında mevcut değil, ekleme yapılamaz.");
            return;
        }

        String insertSql = "INSERT INTO item_ingredients(item_id, ingredient_id) VALUES(?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setInt(1, itemId);
            insertStmt.setInt(2, ingredientId);
            insertStmt.executeUpdate();
            System.out.println("Madde başarıyla ürüne eklendi.");
        } catch (SQLException e) {
            System.out.println("Maddeyi ürüne eklerken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Ürünün veya maddenin var olup olmadığını kontrol eden fonksiyon
    private static boolean doesItemExist(int itemId) {
        String sql = "SELECT COUNT(*) AS count FROM items WHERE id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean exists = rs.getInt("count") > 0;
                System.out.println("Ürün/Madde ID: " + itemId + " mevcut mu: " + exists);
                return exists;
            }
        } catch (SQLException e) {
            System.out.println("Ürün veya madde kontrol edilirken hata oluştu: " + e.getMessage());
            return false;
        }
    }

    // Ürünleri listeleme fonksiyonu (ID'leri de gösterir)
    public static void listAllItems() {
        String sql = "SELECT * FROM items";
        Set<String> displayedItems = new HashSet<>(); // Tekrarları engellemek için Set

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("Mevcut Ürünler ve Maddeler:");
            while (rs.next()) {
                int itemId = rs.getInt("id");  // Ürün ID'sini al
                String itemName = rs.getString("name");
                double itemPrice = rs.getDouble("price");
                if (!displayedItems.contains(itemName)) {
                    System.out.println("ID: " + itemId + " - Ürün/Madde: " + itemName + ", Fiyat: " + itemPrice);
                    displayedItems.add(itemName);
                }
            }

        } catch (SQLException e) {
            System.out.println("Ürünler ve maddeler listelenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Bir ürün için gerekli maddeleri listeleme
    public static void listIngredientsForItem(int itemId) {
        String sql = "SELECT i.id, i.name, i.price FROM items i " +
                "INNER JOIN item_ingredients ii ON i.id = ii.ingredient_id " +
                "WHERE ii.item_id = ?";

        Set<Integer> seenIngredients = new HashSet<>();

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
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

    // Ürün/Madde ismine göre ID'yi al
    public static int getItemIdByName(String listItem) {
        // Split the string at " - " to separate ID and name
        String[] parts = listItem.split(" - ");

        if (parts.length < 2) {
            System.out.println("Invalid format for list item: " + listItem);
            return -1;
        }

        String name = parts[1].trim();  // Take the name part (after the ID)

        String sql = "SELECT id FROM items WHERE TRIM(LOWER(name)) = TRIM(LOWER(?))";
        int itemId = -1;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);  // Only use the name for the query
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                itemId = rs.getInt("id");
                System.out.println("Kontrol Edilen Ürün ID: " + itemId);
            } else {
                System.out.println("Ürün/Madde '" + name + "' veritabanında bulunamadı.");
            }

        } catch (SQLException e) {
            System.out.println("Ürün/Madde ID'si alınırken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }

        return itemId;
    }

    // Bir ürünün malzemelerini al
    public static List<List<String>> getIngredientsForItem(int productId) {
        String sql = "SELECT i.id, i.name, i.price FROM items i INNER JOIN item_ingredients ii ON i.id = ii.ingredient_id WHERE ii.item_id = ?";
        List<List<String>> ingredients = new ArrayList<>();

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                List<String> ingredient = new ArrayList<>();
                ingredient.add(String.valueOf(rs.getInt("id")));  // ID ekle
                ingredient.add(rs.getString("name"));  // Malzeme adı
                ingredient.add(String.valueOf(rs.getDouble("price")));  // Fiyat
                ingredients.add(ingredient);
            }

            // Eklenen malzemeleri kontrol etmek için yazdır
            System.out.println("Alınan Malzemeler: " + ingredients);

        } catch (SQLException e) {
            System.out.println("Malzemeler alınırken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }

        return ingredients;
    }


    public static void deleteItem(int itemId) {
        String deleteRelationsSql = "DELETE FROM item_ingredients WHERE item_id = ? OR ingredient_id = ?";
        String deleteItemSql = "DELETE FROM items WHERE id = ?";

        try (Connection conn = DatabaseManager.connect()) {
            // İlk olarak, ürünle ilişkili kayıtları sil
            try (PreparedStatement pstmt = conn.prepareStatement(deleteRelationsSql)) {
                pstmt.setInt(1, itemId);
                pstmt.setInt(2, itemId);
                pstmt.executeUpdate();
            }

            // Sonrasında ürünü sil
            try (PreparedStatement pstmt = conn.prepareStatement(deleteItemSql)) {
                pstmt.setInt(1, itemId);
                pstmt.executeUpdate();
                System.out.println("Ürün/madde başarıyla silindi.");
            }
        } catch (SQLException e) {
            System.out.println("Ürün/madde silinirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void updateItemPrice(int itemId, double newPrice) {
        String sql = "UPDATE items SET price = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, itemId);
            pstmt.executeUpdate();
            System.out.println("Ürün/madde fiyatı başarıyla güncellendi.");
        } catch (SQLException e) {
            System.out.println("Ürün/madde fiyatı güncellenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Tüm ürün ve maddelerin ID'leriyle birlikte isimlerini döndür
    public static List<List<String>> getAllItemsWithIDs() {
        String sql = "SELECT id, name FROM items";
        List<List<String>> items = new ArrayList<>();

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                List<String> item = new ArrayList<>();
                item.add(String.valueOf(rs.getInt("id"))); // ID ekle
                item.add(rs.getString("name")); // İsim ekle
                items.add(item);
            }

        } catch (SQLException e) {
            System.out.println("Ürünler ve maddeler alınırken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    public static double getSalePriceForItem(int itemId) {
        String sql = "SELECT price FROM items WHERE id = ?";
        double salePrice = 0;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                salePrice = rs.getDouble("price");  // Ürünün satış fiyatını alıyoruz
            }

        } catch (SQLException e) {
            System.out.println("Satış fiyatı alınırken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }

        return salePrice;
    }
    public static double getTotalPriceForProduct(int productId) {
        String sql = "SELECT SUM(i.price) FROM items i " +
                "INNER JOIN item_ingredients ii ON i.id = ii.ingredient_id " +
                "WHERE ii.item_id = ?";

        double totalPrice = 0.0;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                totalPrice = rs.getDouble(1); // Toplam fiyatı alıyoruz
            }

        } catch (SQLException e) {
            System.out.println("Toplam fiyat hesaplanırken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }

        return totalPrice;
    }
}
