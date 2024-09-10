package com.example.bakerymanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;

import java.util.List;
import java.util.Optional;

public class MainController {

    @FXML
    private ListView<String> itemListView;

    @FXML
    private TextField searchField;

    private List<String> allItems;  // Tüm ürünler ve maddeleri burada tutacağız.

    @FXML
    public void initialize() {
        // Uygulama başladığında ürün ve maddeleri ListView'e ekleyelim
        loadItemsIntoListView();
    }

    // Veritabanındaki tüm ürün ve maddeleri alıp ListView'e yükleyen metod
    public void loadItemsIntoListView() {
        itemListView.getItems().clear();  // Listeyi temizle
        allItems = ItemService.getAllItems();  // Tüm ürün/madde isimlerini al
        itemListView.getItems().addAll(allItems);  // ListView'e ekle
    }

    // Arama çubuğu ile ürünleri filtreleme
    @FXML
    public void filterItems() {
        String searchTerm = searchField.getText().toLowerCase();
        itemListView.getItems().clear();

        for (String item : allItems) {
            if (item.toLowerCase().contains(searchTerm)) {
                itemListView.getItems().add(item);
            }
        }
    }

    // Ürün/Madde ekleme butonuna basıldığında
    @FXML
    public void addItem() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ürün/Madde Ekle");
        dialog.setHeaderText("Yeni ürün veya madde ekleyin");
        dialog.setContentText("Ürün/Madde ismi:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String name = result.get();
            // Fiyatı soralım
            TextInputDialog priceDialog = new TextInputDialog();
            priceDialog.setTitle("Fiyat Girin");
            priceDialog.setContentText("Fiyat:");
            Optional<String> priceResult = priceDialog.showAndWait();
            if (priceResult.isPresent()) {
                double price = Double.parseDouble(priceResult.get());
                ItemService.addItem(name, price);
                loadItemsIntoListView();  // Listeyi güncelle
            }
        }
    }

    // Ürüne madde ekleme butonuna basıldığında
    @FXML
    public void addIngredientToItem() {
        String selectedItem = itemListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int productId = ItemService.getItemIdByName(selectedItem);
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Madde ID'si Girin");
            dialog.setContentText("Eklemek istediğiniz maddenin ID'sini girin:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                int ingredientId = Integer.parseInt(result.get());
                ItemService.addIngredientToItem(productId, ingredientId);
                loadItemsIntoListView();  // Listeyi güncelle
            }
        }
    }

    // Ürüne çift tıklama ile malzeme ve fiyat bilgilerini gösterme
    @FXML
    public void handleItemClick(MouseEvent event) {
        if (event.getClickCount() == 2) {  // Çift tıklama kontrolü
            String selectedItem = itemListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                int productId = ItemService.getItemIdByName(selectedItem);
                List<String> ingredients = ItemService.getIngredientsForItem(productId);

                if (ingredients.isEmpty()) {
                    showAlert("Bu ürün için madde bulunmamaktadır.");
                } else {
                    StringBuilder ingredientInfo = new StringBuilder();
                    for (String ingredient : ingredients) {
                        ingredientInfo.append(ingredient).append("\n");
                    }
                    showAlert("Ürün: " + selectedItem + "\n\nMalzemeler:\n" + ingredientInfo.toString());
                }
            }
        }
    }

    // Ürünü silme işlemi
    @FXML
    public void deleteItem() {
        String selectedItem = itemListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int itemId = ItemService.getItemIdByName(selectedItem);
            ItemService.deleteItem(itemId);
            loadItemsIntoListView();  // Listeyi güncelle
        } else {
            showAlert("Silinecek bir ürün/madde seçilmedi.");
        }
    }

    // Fiyat güncelleme işlemi
    @FXML
    public void updateItemPrice() {
        String selectedItem = itemListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int itemId = ItemService.getItemIdByName(selectedItem);
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Yeni Fiyat Girin");
            dialog.setContentText("Yeni fiyat:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                double newPrice = Double.parseDouble(result.get());
                ItemService.updateItemPrice(itemId, newPrice);
                loadItemsIntoListView();  // Listeyi güncelle
            }
        } else {
            showAlert("Fiyatı güncellenecek bir ürün/madde seçilmedi.");
        }
    }

    // Kullanıcıya mesaj göstermek için alert fonksiyonu
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Bilgilendirme");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
