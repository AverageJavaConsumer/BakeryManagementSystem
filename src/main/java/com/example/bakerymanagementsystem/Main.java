package com.example.bakerymanagementsystem;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean keepRunning = true;

        // Veritabanına bağlan ve tabloları oluştur
        DatabaseManager.createTables();

        // Program döngüsü
        while (keepRunning) {
            System.out.println("\n===== Fırın Yönetim Sistemi =====");
            System.out.println("1 - Ürün Ekle");
            System.out.println("2 - Ürünleri Görüntüle");
            System.out.println("3 - Ürüne Madde Ekle");
            System.out.println("4 - Bir Ürünün Maddelerini Görüntüle");
            System.out.println("5 - Bir Maddenin Alt Maddelerini Görüntüle");
            System.out.println("6 - Çıkış");
            System.out.print("Seçiminiz: ");

            String input = scanner.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Lütfen geçerli bir sayı girin.");
                continue;
            }

            switch (choice) {
                case 1:
                    // Ürün ekleme
                    System.out.print("Ürün adını girin: ");
                    String productName = scanner.nextLine();
                    double productPrice;
                    while (true) {
                        System.out.print("Ürün fiyatını girin: ");
                        String priceInput = scanner.nextLine();
                        try {
                            productPrice = Double.parseDouble(priceInput);
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Lütfen geçerli bir fiyat girin.");
                        }
                    }
                    ProductService.addProduct(productName, productPrice);
                    break;

                case 2:
                    // Ürünleri listeleme
                    ProductService.listAllProducts();
                    break;

                case 3:
                    // Ürüne madde ekleme
                    System.out.print("Maddeyi eklemek istediğiniz ürünün ID'sini girin: ");
                    int productId;
                    try {
                        productId = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Geçersiz ürün ID'si.");
                        break;
                    }

                    System.out.print("Eklemek istediğiniz maddenin ID'sini girin: ");
                    int ingredientId;
                    try {
                        ingredientId = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Geçersiz madde ID'si.");
                        break;
                    }

                    ProductService.addIngredientToProduct(productId, ingredientId);
                    break;

                case 4:
                    // Bir ürün için maddeleri listeleme
                    System.out.print("Lütfen ürün ID'sini girin: ");
                    int listProductId;
                    try {
                        listProductId = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Geçersiz ürün ID'si.");
                        break;
                    }
                    IngredientService.listIngredientsForProduct(listProductId);
                    break;

                case 5:
                    // Bir madde için alt maddeleri listeleme
                    System.out.print("Lütfen madde ID'sini girin: ");
                    int listIngredientId;
                    try {
                        listIngredientId = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Geçersiz madde ID'si.");
                        break;
                    }
                    IngredientService.listSubIngredients(listIngredientId);
                    break;

                case 6:
                    // Çıkış
                    keepRunning = false;
                    System.out.println("Programdan çıkılıyor. İyi günler!");
                    break;

                default:
                    System.out.println("Geçersiz seçim, lütfen tekrar deneyin.");
            }
        }

        scanner.close();
    }
}
