package com.example.project;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class FoodModel {
    ArrayList<Food> foodList = new ArrayList<>();

    // поле фильтр, по умолчанию используем базовый класс
    //
    Class<? extends Food> foodFilter = Food.class;
    public void setFoodFilter(Class<? extends Food> foodFilter) {
        this.foodFilter = foodFilter;

        this.emitDataChanged();
    }
    private int counter = 1; // добавили счетчик
    // Создаем наш любимый функциональный интерфейс
    // с помощью него мы организуем общение между моделью и контроллером
    public interface DataChangedListener {
        void dataChanged(ArrayList<Food> foodList);
    }

    // Меняем на private и делаем список
    private ArrayList<DataChangedListener> dataChangedListeners = new ArrayList<>();
    // добавляем метод который позволяет привязать слушателя
    public void addDataChangedListener(DataChangedListener listener) {
        // ну просто его в список добавляем
        this.dataChangedListeners.add(listener);
    }
    public void load() {
        foodList.clear();

        // добавили второй параметр как false
        this.add(new Fruit(100, "Яблоко", true), false);
        this.add(new Chocolate(200, "шоколад Аленка", Chocolate.Type.milk), false);
        this.add(new Cookie(300, "сладкая булочка с Маком", true, true, false), false);

        this.emitDataChanged();

    }
    public void edit (Food food){
        // ищем объект в списке
        for (int i = 0; i < this.foodList.size(); ++i) {
            // чтобы id совпадал с id переданным форме
            if (this.foodList.get(i).id == food.id) {
                // если нашли, то подменяем объект
                this.foodList.set(i, food);
                break;
            }
        }

        // а тут добавляем
        // если к dataChangedListener привязали функцию
        // а тут делаем цикл по всем слушателям
        this.emitDataChanged();
    }

    // добавили метод
    // добавили параметр emit в метод,
// если там true то вызывается оповещение слушателей
    public void add(Food food, boolean emit) {
        food.id = counter;
        counter += 1;

        this.foodList.add(food);

        if (emit) {
            this.emitDataChanged();
        }
    }
    public void add(Food food) {
        add(food, true);
    }

    // для удаления достаточно идентификатора
    public void delete(int id)
    {
        for (int i = 0; i< this.foodList.size(); ++i) {
            // ищем в цикле еду с данным айдишником
            if (this.foodList.get(i).id == id) {
                // если нашли то удаляем
                this.foodList.remove(i);
                break;
            }
        }

        // оповещаем об изменениях
        this.emitDataChanged();
    }

    private void emitDataChanged() {
        for (DataChangedListener listener : dataChangedListeners) {
            ArrayList<Food> filteredList = new ArrayList<>(
                    foodList.stream() // запускаем стрим
                            .filter(food -> foodFilter.isInstance(food)) // фильтруем по типу
                            .collect(Collectors.toList()) // превращаем в список
            );
            listener.dataChanged(filteredList); // подсовывам сюда список
        }
    }

    public void saveToFile(String path) {
        try (Writer writer =  new FileWriter(path)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerFor(new TypeReference<ArrayList<Food>>() { }) // указали какой тип подсовываем
                    .withDefaultPrettyPrinter() // кстати эта строчка чтобы в файлике все красиво печаталось
                    .writeValue(writer, foodList); // а это непосредственно запись
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile(String path) {
        try (Reader reader =  new FileReader(path)) {
            ObjectMapper mapper = new ObjectMapper();
            foodList = mapper.readerFor(new TypeReference<ArrayList<Food>>() { })
                    .readValue(reader);

            // рассчитываем счетчик как максимальное значение id плюс 1
            this.counter = foodList.stream()
                    .map(food -> food.id)
                    .max(Integer::compareTo)
                    .orElse(0) + 1;
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.emitDataChanged();
    }

    }
