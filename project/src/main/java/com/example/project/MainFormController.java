package com.example.project;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainFormController implements Initializable {
    public TableView mainTable;
    public ComboBox cmbFoodType; // добавил комбобокс
    // можно удалить этот список, наш контроллер больше данных не хранит
    //ObservableList<Food> foodList = FXCollections.observableArrayList();
    FoodModel foodModel = new FoodModel();

    ObservableList<Class<? extends Food>> foodTypes = FXCollections.observableArrayList(
            Food.class,
            Fruit.class,
            Chocolate.class,
            Cookie.class
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        foodModel.addDataChangedListener(foods -> {
            mainTable.setItems(FXCollections.observableArrayList(foods));
        });
        // foodModel.load(); закоментили

// привязали список
        cmbFoodType.setItems(foodTypes);
        cmbFoodType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.foodModel.setFoodFilter((Class<? extends Food>) newValue);
        });

        // переопределил метод преобразования имени класса в текст
        cmbFoodType.setConverter(new StringConverter<Class>() {
            @Override
            public String toString(Class object) {
                // просто перебираем тут все возможные варианты
                if (Food.class.equals(object)) {
                    return "Все";
                } else if (Fruit.class.equals(object)) {
                    return "Фрукты";
                } else if (Chocolate.class.equals(object)) {
                    return "Шоколад";
                } else if (Cookie.class.equals(object)) {
                    return "Булочка";
                }
                return null;
            }

            @Override
            public Class fromString(String string) {
                return null;
            }
        });

        // создаем столбец, указываем что столбец преобразует Food в String,
        // указываем заголовок колонки как "Название"
        TableColumn<Food, String> titleColumn = new TableColumn<>("Название");
        // указываем какое поле брать у модели Food
        // в данном случае title, кстати именно в этих целях необходимо было создать getter и setter для поля title
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        // тоже самое для калорийности
        TableColumn<Food, String> kkalColumn = new TableColumn<>("Калорийность");
        kkalColumn.setCellValueFactory(new PropertyValueFactory<>("kkal"));

        // добавляем столбец с описанием
        TableColumn<Food, String> descriptionColumn = new TableColumn<>("Описание");
        // если хотим что-то более хитрое выводить, то используем лямбда выражение
        descriptionColumn.setCellValueFactory(cellData -> {
            // плюс надо обернуть возвращаемое значение в обертку свойство
            return new SimpleStringProperty(cellData.getValue().getDescription());
        });

        // добавляем сюда descriptionColumn
        mainTable.getColumns().addAll(titleColumn, kkalColumn, descriptionColumn);

    }

    // добавляем инфу что наш код может выбросить ошибку IOException
    public void onAddClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("FoodForm.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(this.mainTable.getScene().getWindow());

        // сначала берем контроллер
        FoodFormController controller = loader.getController();
        // передаем модель
        controller.foodModel = foodModel;

        // показываем форму
        stage.showAndWait();


    //это не нужно больше
   // if (controller.getModalResult()) {
      //  Food newFood = controller.getFood();
     //   this.foodList.add(newFood);
  //  }
    }

    public void onEditClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("FoodForm.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(this.mainTable.getScene().getWindow());

        FoodFormController controller = loader.getController();
        controller.setFood((Food) this.mainTable.getSelectionModel().getSelectedItem());
        controller.foodModel = foodModel; // передаем модель в контроллер

        stage.showAndWait();

    /*
    это не нужно больше
    if (controller.getModalResult()) {
        int index = this.mainTable.getSelectionModel().getSelectedIndex();
        this.mainTable.getItems().set(index, controller.getFood());
    }
    */
    }

    public void onDeleteClick(ActionEvent actionEvent) {
        // берем выбранную на форме еду
        Food food = (Food) this.mainTable.getSelectionModel().getSelectedItem();

        // выдаем подтверждающее сообщение
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(String.format("Точно удалить %s?", food.getTitle()));

        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) {
            foodModel.delete(food.id); // тут вызываем метод модели, и передаем идентификатор
        }
    }

    public void onSaveToFileClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить данные");
        fileChooser.setInitialDirectory(new File("."));


        // тут вызываем диалог для сохранения файла
        File file = fileChooser.showSaveDialog(mainTable.getScene().getWindow());

        if (file != null) {
            foodModel.saveToFile(file.getPath());
        }
    }

    public void onLoadFromFileClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить данные");
        fileChooser.setInitialDirectory(new File("."));

        // а тут диалог для открытия файла
        File file = fileChooser.showOpenDialog(mainTable.getScene().getWindow());

        if (file != null) {
            foodModel.loadFromFile(file.getPath());
        }
    }

}

