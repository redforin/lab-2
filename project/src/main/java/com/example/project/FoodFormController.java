package com.example.project;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class FoodFormController implements Initializable {
    public FoodModel foodModel; // добавили
    // создаем
    public ChoiceBox cmbFoodType;
    public TextField txtFoodTitle;
    public TextField txtFoodKkal;

    public VBox fruitPane;
    public CheckBox chkIsFresh;

    public HBox chocolatePane;
    public ChoiceBox cmbChocolateType;

    public VBox cookiePane;
    public CheckBox chkWithSugar;
    public CheckBox chkWithPoppy;
    public CheckBox chkWithSesame;

    final String FOOD_FRUIT = "Фрукт";
    final String FOOD_CHOCOLATE = "Шоколадка";
    final String FOOD_COOKIE = "Булочка";

    // private Boolean modalResult = false; // УДАЛЯЕМ ЭТО ПОЛЕ
    private Integer id = null;

    public void onCancelClick(ActionEvent actionEvent) {
        // this.modalResult = false; // ЭТУ СТРОЧКУ ТОЖЕ УДАЛЯЕМ
        ((Stage)((Node)actionEvent.getSource()).getScene().getWindow()).close();
    }

    /*
    И ЭТОТ МЕТОД БОЛЬШЕ НЕ НУЖЕН
    public Boolean getModalResult() {
        return modalResult;
    }
    */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbFoodType.setItems(FXCollections.observableArrayList(
                FOOD_FRUIT,
                FOOD_CHOCOLATE,
                FOOD_COOKIE
        ));

        cmbFoodType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updatePanes((String) newValue);
        });

        // добавляем все три типа шоколада в комобобокс
        cmbChocolateType.getItems().setAll(
                Chocolate.Type.white,
                Chocolate.Type.black,
                Chocolate.Type.milk
        );

        // и используем метод setConverter,
        // чтобы типы объекты рендерились как строки
       // cmbChocolateType.setConverter(new StringConverter<Chocolate.Type>() {
         //   @Override
       //     public String toString(Chocolate.Type object) {
                // просто указываем как рендерить
           //     switch (object) {
           //         case white:
                  //      return "Белый";
               //     case black:
                  //      return "Черный";
                 //   case milk:
              //          return "Молочный";
              //  }
             //   return null;
          //  }

          //  @Override
         //   public Chocolate.Type fromString(String string) {
                // этот метод не трогаем так как наш комбобкос имеет фиксированный набор элементов
           //     return null;
       //     }
      //  });

        // вызываю новую функцию при инициализации формы
        updatePanes("");
    }

    // добавил новую функцию
    public void updatePanes(String value) {
        this.fruitPane.setVisible(value.equals(FOOD_FRUIT));
        this.fruitPane.setManaged(value.equals(FOOD_FRUIT));
        this.chocolatePane.setVisible(value.equals(FOOD_CHOCOLATE));
        this.chocolatePane.setManaged(value.equals(FOOD_CHOCOLATE));
        this.cookiePane.setVisible(value.equals(FOOD_COOKIE));
        this.cookiePane.setManaged(value.equals(FOOD_COOKIE));
    }

    // обработчик нажатия на кнопку Сохранить
    public void onSaveClick(ActionEvent actionEvent) {
        // проверяем передали ли идентификатор
        if (this.id != null) {
            // если передавали значит у нас редактирование
            // собираем еду с формы
            Food food = getFood();
            // подвязываем переданный идентификатор к собранной с формы еды
            food.id = this.id;
            // отправляем в модель на изменение
            this.foodModel.edit(food);
        } else {
            // ну а если у нас добавление, просто добавляем объект
            this.foodModel.add(getFood());
        }
        ((Stage)((Node)actionEvent.getSource()).getScene().getWindow()).close();
    }

    public void setFood(Food food) {
        this.cmbFoodType.setDisable(food != null);

        // присвоим значение идентификатора,
        // если передали еду то есть food != null, то используем food.id
        // иначе запихиваем в this.id значение null
        this.id = food != null ? food.id : null;

        if (food != null) {
            // ну а тут стандартное заполнение полей в соответствии с переданной едой
            this.txtFoodKkal.setText(String.valueOf(food.getKkal()));
            this.txtFoodTitle.setText(food.getTitle());

            if (food instanceof Fruit) { // если фрукт
                this.cmbFoodType.setValue(FOOD_FRUIT);
                this.chkIsFresh.setSelected(((Fruit) food).isFresh);
            } else if (food instanceof Cookie) { // если булочка
                this.cmbFoodType.setValue(FOOD_COOKIE);
                this.chkWithSugar.setSelected(((Cookie) food).withSugar);
                this.chkWithPoppy.setSelected(((Cookie) food).withPoppy);
                this.chkWithSesame.setSelected(((Cookie) food).withSesame);
            } else if (food instanceof Chocolate) { // если шоколад
                this.cmbFoodType.setValue(FOOD_CHOCOLATE);
                this.cmbChocolateType.setValue(((Chocolate) food).type);
            }
        }
    }

    public Food getFood() {
        Food result = null;
        int kkal = Integer.parseInt(this.txtFoodKkal.getText());
        String title = this.txtFoodTitle.getText();

        switch ((String) this.cmbFoodType.getValue()) {
            case FOOD_CHOCOLATE:
                result = new Chocolate(kkal, title, (Chocolate.Type) this.cmbChocolateType.getValue());
                break;
            case FOOD_COOKIE:
                result = new Cookie(
                        kkal,
                        title,
                        this.chkWithSugar.isSelected(),
                        this.chkWithPoppy.isSelected(),
                        this.chkWithSesame.isSelected()
                );
                break;
            case FOOD_FRUIT:
                result = new Fruit(kkal, title, this.chkIsFresh.isSelected());
                break;
        }
        return result;
    }
}