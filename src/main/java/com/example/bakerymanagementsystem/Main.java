package com.example.bakerymanagementsystem;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Veritabanına bağlan ve tabloları oluştur
        DatabaseManager.createTables();

        // Örnek ürün ve madde ekle
        ProductService.addProduct("Doğum Günü Pastası", 150.0);
        IngredientService.addIngredient("Kek", null);
        IngredientService.addIngredient("Krem Şanti", null);
        IngredientService.addIngredient("Un", 1); // Kek'in alt maddesi
        IngredientService.addIngredient("Süt", 1); // Kek'in alt maddesi
        IngredientService.addIngredient("Şeker", 2); // Krem şantinin alt maddesi
        IngredientService.addIngredient("Krema", 2); // Krem şantinin alt maddesi

        // Ürünleri listeleme
        System.out.println("Tüm ürünleri görmek ister misiniz? (evet/hayır)");
        if (scanner.nextLine().equalsIgnoreCase("evet")) {
            ProductService.listAllProducts();
        }

        // Belirli bir ürün için maddeleri listeleme
        System.out.println("Bir ürün için maddeleri listelemek ister misiniz? (evet/hayır)");
        if (scanner.nextLine().equalsIgnoreCase("evet")) {
            System.out.println("Lütfen ürün ID'sini girin:");
            int productId = scanner.nextInt();
            scanner.nextLine();  // Satır sonu karakterini temizlemek için
            IngredientService.listIngredientsForProduct(productId);
        }

        // Belirli bir madde için alt maddeleri listeleme
        System.out.println("Bir madde için alt maddeleri görmek ister misiniz? (evet/hayır)");
        if (scanner.nextLine().equalsIgnoreCase("evet")) {
            System.out.println("Lütfen madde ID'sini girin:");
            int ingredientId = scanner.nextInt();
            scanner.nextLine();  // Satır sonu karakterini temizlemek için
            IngredientService.listSubIngredients(ingredientId);
        }
    }
}
