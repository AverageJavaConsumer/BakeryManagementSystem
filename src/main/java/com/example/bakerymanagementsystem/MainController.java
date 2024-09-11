package com.example.bakerymanagementsystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert.AlertType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;


public class MainController {

    @FXML
    private ListView<String> itemListView;

    @FXML
    private TextField searchField;

    private List<String> allItems;  // Tüm ürünler ve maddeleri burada tutacağız.

    @FXML
    private TableView<List<String>> ingredientTableView;

    @FXML
    private TableColumn<List<String>, String> ingredientNameColumn;

    @FXML
    private TableColumn<List<String>, String> ingredientPriceColumn;
    @FXML
    private Spinner<Double> quantitySpinner;

    @FXML
    private Label totalPriceLabel;
    @FXML
    private Label finalPriceLabel;

    // Seçilen ürünlerin tutulduğu stack, geri dönüş işlemi için
    private Stack<Integer> itemHistory = new Stack<>();


    @FXML
    public void initialize() {
        // Uygulama başladığında ürün ve maddeleri ListView'e ekleyelim
        loadItemsIntoListView();
        // Spinner için değerleri ayarlıyoruz
        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 100, 1, 0.5);
        quantitySpinner.setValueFactory(valueFactory);

        // Spinner değer değiştiğinde fiyatı hesapla
        quantitySpinner.valueProperty().addListener((observable, oldValue, newValue) -> updateTotalPrice());
    }


    // Veritabanındaki tüm ürün ve maddeleri alıp ListView'e yükleyen metod
    public void loadItemsIntoListView() {
        itemListView.getItems().clear();  // Listeyi temizle
        List<List<String>> allItemsWithIDs = ItemService.getAllItemsWithIDs();  // Tüm ürün/madde isimlerini ve ID'lerini al

        allItems = new ArrayList<>();  // allItems listesini başlatıyoruz

        for (List<String> item : allItemsWithIDs) {
            String listItem = item.get(0) + " - " + item.get(1);
            itemListView.getItems().add(listItem);  // ID ve ismi ListView'e ekle
            allItems.add(listItem);  // Aynı öğeyi allItems listesine de ekle
        }
    }



    // Arama çubuğu ile ürünleri filtreleme
    @FXML
    public void filterItems() {
        String searchTerm = searchField.getText().toLowerCase();
        itemListView.getItems().clear();

        // allItems'in null olmadığından emin ol
        if (allItems != null) {
            for (String item : allItems) {
                if (item.toLowerCase().contains(searchTerm)) {
                    itemListView.getItems().add(item);
                }
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
    @FXML
    public void goBackToPreviousItem() {
        if (!previousProducts.isEmpty()) {
            int previousProductId = previousProducts.pop();
            loadIngredientsForSelectedItem(previousProductId);
        }
    }

    // Bu metod, TableView'e malzemeleri yükler.
    public void loadIngredientsForSelectedItem(int productId) {
        List<List<String>> ingredients = ItemService.getIngredientsForItem(productId);

        // Stack'te zaten bu ürün varsa tekrar eklemeyelim.
        if (previousProducts.isEmpty() || previousProducts.peek() != productId) {
            previousProducts.push(productId);
            System.out.println("Ürün stack'e eklendi, ID: " + productId);
        }

        ingredientTableView.getItems().clear(); // Önceki verileri temizle
        ingredientTableView.getItems().addAll(ingredients); // Yeni verileri ekle

        ingredientNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));  // İsim 2. indexte
        ingredientPriceColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));  // Fiyat 3. indexte

        // Toplam fiyatı güncelle
        updateTotalPriceForProduct(productId);

        // Çift tıklama ile alt malzemeleri almak için
        ingredientTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                List<String> selectedIngredient = ingredientTableView.getSelectionModel().getSelectedItem();
                if (selectedIngredient != null) {
                    int ingredientId = Integer.parseInt(selectedIngredient.get(0));  // ID'yi al

                    // Seçilen alt malzemeyi yükle
                    loadIngredientsForSelectedItem(ingredientId);
                }
            }
        });
    }



    // Geri dönmek için önceki ürünleri depolamak için bir liste
    private Stack<Integer> previousProducts = new Stack<>();
    @FXML
    public void handleItemClick(MouseEvent event) {
        if (event.getClickCount() == 2) {  // Çift tıklama kontrolü
            String selectedItem = itemListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                // "ID - İsim" formatında olan item'ı bölüp ID'yi alıyoruz
                String[] parts = selectedItem.split(" - ");
                int productId = Integer.parseInt(parts[0]);  // ID'yi al

                // Ürünü stack'e ekle
                if (!previousProducts.isEmpty() && previousProducts.peek() != productId) {
                    previousProducts.push(productId);  // Stack'e sadece yeni ürünü ekle
                    System.out.println("Ürün stack'e eklendi, ID: " + productId);
                }

                loadIngredientsForSelectedItem(productId);  // Malzemeleri TableView'e yükle
            }
        }
    }

    // Geri Dön butonuna basıldığında çalışacak fonksiyon
    @FXML
    public void handleBackButtonClick() {
        if (!previousProducts.isEmpty()) {
            // Stack'teki son ürünü çıkarıyoruz
            int previousProductId = previousProducts.pop();
            System.out.println("Geri dönülüyor, ID: " + previousProductId);

            // Stack'in şu anki durumunu yazdıralım
            System.out.println("Stack'teki ürünler: " + previousProducts);

            // Eğer stack'te önceki ürün varsa, onu gösteriyoruz
            if (!previousProducts.isEmpty()) {
                previousProductId = previousProducts.peek();  // En son eklenen ürünü al
                System.out.println("Geri dönüldü, gösterilecek ürün ID'si: " + previousProductId);
                loadIngredientsForSelectedItem(previousProductId);  // Malzemeleri yükle
                updateTotalPriceForProduct(previousProductId);  // Toplam fiyatı güncelle
            } else {
                // Stack boşsa, orijinal ürün/malzeme listesini göster
                System.out.println("Stack boş, orijinal liste gösteriliyor.");
                loadItemsIntoListView();
                ingredientTableView.getItems().clear();  // Malzeme listesini de temizle
                totalPriceLabel.setText("Toplam Hammadde Fiyatı: 0,00");
                finalPriceLabel.setText("Satış Fiyatı: 0,00");
            }
        } else {
            // Stack boşsa, geri dönecek bir şey yok
            System.out.println("Geri dönülecek önceki bir ürün yok.");
        }
    }

    // Fiyatları dinamik olarak güncelleme fonksiyonu
    @FXML
    private void updateTotalPrice() {
        if (!previousProducts.isEmpty()) {
            // Stack'teki en son ürünü al
            int currentProductId = previousProducts.peek();
            updateTotalPriceForProduct(currentProductId); // Stack'teki son ürüne göre fiyat güncellemesi yap
        } else {
            // Eğer stack boşsa, seçili ürüne göre fiyat güncelle
            String selectedItem = itemListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                // Seçilen ürün ID'sini ayır ve al
                String[] parts = selectedItem.split(" - ");
                int productId = Integer.parseInt(parts[0]);

                // Seçilen ürün için toplam fiyatı hesapla
                updateTotalPriceForProduct(productId);
            }
        }
    }


    @FXML
    private void updateTotalPriceForProduct(int productId) {
        // Toplam hammadde fiyatını çek
        double totalPrice = ItemService.getTotalPriceForProduct(productId);

        // Satış fiyatını veritabanından çek
        double salePrice = ItemService.getSalePriceForItem(productId);

        // Miktarı al
        double quantity = quantitySpinner.getValue();

        // Hammadde ve satış fiyatlarını hesapla
        double finalTotalPrice = totalPrice * quantity;
        double finalSalePrice = salePrice * quantity;

        // Fiyatları label'da göster
        totalPriceLabel.setText("Toplam Hammadde Fiyatı: " + String.format("%.2f", finalTotalPrice));
        finalPriceLabel.setText("Satış Fiyatı: " + String.format("%.2f", finalSalePrice));
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
