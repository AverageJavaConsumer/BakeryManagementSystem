package com.example.bakerymanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Scanner;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("layout.fxml"));
        primaryStage.setTitle("Fırın Yönetim Sistemi");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        Thread fxThread = new Thread(() -> launch(args));
        fxThread.setDaemon(true);  // Ana thread durduğunda JavaFX thread'inin de durmasını sağlamak için daemon yapıyoruz
        fxThread.start();

        // Konsol tabanlı işlemler
        Scanner scanner = new Scanner(System.in);
        boolean keepRunning = true;

        // Veritabanına bağlan ve tabloları oluştur
        DatabaseManager.createTables();

        while (keepRunning) {
            // Menü güncelleniyor
            System.out.println("\n===== Fırın Yönetim Sistemi =====");
            System.out.println("1 - Ürün veya Madde Ekle");
            System.out.println("2 - Ürünleri ve Maddeleri Görüntüle");
            System.out.println("3 - Ürüne Madde Ekle");
            System.out.println("4 - Bir Ürünün Maddelerini Görüntüle");
            System.out.println("5 - Ürün/Madde Fiyatını Güncelle");
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
                    // Ürün veya madde ekleme
                    System.out.print("Ürün veya madde adını girin: ");
                    String itemName = scanner.nextLine();
                    double itemPrice;
                    while (true) {
                        System.out.print("Fiyatı girin: ");
                        String priceInput = scanner.nextLine();
                        try {
                            itemPrice = Double.parseDouble(priceInput);
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Lütfen geçerli bir fiyat girin.");
                        }
                    }
                    ItemService.addItem(itemName, itemPrice);
                    break;

                case 2:
                    // Ürünleri ve maddeleri listeleme
                    ItemService.listAllItems();
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

                    ItemService.addIngredientToItem(productId, ingredientId);
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
                    ItemService.listIngredientsForItem(listProductId);
                    break;

                case 5:
                    // Ürün/Madde fiyatını güncelleme
                    System.out.print("Fiyatını güncellemek istediğiniz ürün veya madde ID'sini girin: ");
                    int updateItemId;
                    try {
                        updateItemId = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Geçersiz ID.");
                        break;
                    }

                    System.out.print("Yeni fiyatı girin: ");
                    double newPrice;
                    try {
                        newPrice = Double.parseDouble(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Geçersiz fiyat.");
                        break;
                    }

                    ItemService.updateItemPrice(updateItemId, newPrice);
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
