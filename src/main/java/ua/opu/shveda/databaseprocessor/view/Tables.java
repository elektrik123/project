package ua.opu.shveda.databaseprocessor.view;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import ua.opu.shveda.databaseprocessor.model.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static ua.opu.shveda.databaseprocessor.persistance.WorkerRepository.*;

public class Tables {
    private static class TableBuilder<T> {
        private TableView<T> table;

        public TableBuilder() {
            this.table = new TableView<>();
        }

        TableView<T> build() {
            return table;
        }

        TableBuilder<T> withStringCol(String name, String property) {
            var col = new TableColumn<T, String>(name);
            col.setCellValueFactory(new PropertyValueFactory<>(property));
            table.getColumns().add(col);
            return this;
        }

        TableBuilder<T> withIntCol(String name, String property) {
            var col = new TableColumn<T, Integer>();
            col.setCellValueFactory(new PropertyValueFactory<>(property));
            table.getColumns().add(col);
            return this;
        }

        TableBuilder<T> withWidth(int width) {
            table.setPrefWidth(width);
            return this;
        }

        TableBuilder<T> withDateCol(String name, String property) {
            var col = new TableColumn<T, Date>(name);
            col.setCellFactory(Tables::dateFormatFactory);
            col.setCellValueFactory(new PropertyValueFactory<>(property));
            table.getColumns().add(col);
            return this;
        }

        TableBuilder<T> withDateTimeCol(String name, String property) {
            var col = new TableColumn<T, LocalDateTime>(name);
            col.setCellFactory(Tables::dateTimeFormatFactory);
            col.setCellValueFactory(new PropertyValueFactory<>(property));
            table.getColumns().add(col);
            return this;
        }
    }


    public static TableView<WorkShift> workShiftTable() {
        return new TableBuilder<WorkShift>()
                .withIntCol("Ід", "id")
                .withDateCol("Дата", "date")
                .withStringCol("Д/Н", "daytime")
                .build();
    }

    public static TitledPane brigadePane(Brigade brigade) {
        HBox root = new HBox(10);
        TitledPane pane = new TitledPane("Бригада №", root);
        Label number = new Label(String.valueOf(brigade.id()));
        pane.setGraphic(number);
        pane.setContentDisplay(ContentDisplay.RIGHT);

        var workers = workersTable();
        workers.setItems(FXCollections.observableList(brigade.workers()));

        var workShifts = workShiftTable();
        workShifts.setItems(FXCollections.observableList(brigade.workShifts()));

        root.getChildren().add(workers);
        root.getChildren().add(workShifts);
        return pane;
    }

    public static TableView<User> usersTable() {
        return new TableBuilder<User>()
                .withStringCol("Логін", "login")
                .withStringCol("Роль", "role")
                .build();
    }

    public static TableView<Worker> workersTable() {
        return new TableBuilder<Worker>()
                .withIntCol("Id", "id")
                .withStringCol("Ім'я", "name")
                .withStringCol("Адреса", "address")
                .withStringCol("Телефон", "phone")
                .withStringCol("Посада", "post")
                .withWidth(575)
                .build();
    }

    private static <T> TableCell<T, Date> dateFormatFactory(TableColumn<T, Date> column) {
        return new TableCell<>() {
            private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    this.setText(format.format(item));

                }
            }
        };
    }

    public static TableView<AccidentRequest> accidentTable() {
        TableView<AccidentRequest> table = new TableView<>();
        var idCol = new TableColumn<AccidentRequest, Integer>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        table.getColumns().add(idCol);

        var costCol = new TableColumn<AccidentRequest, Double>("Вартість");
        costCol.setCellValueFactory(new PropertyValueFactory<>("cost"));
        table.getColumns().add(costCol);

        var bonusCol = new TableColumn<AccidentRequest, Double>("Премія");
        bonusCol.setCellValueFactory(new PropertyValueFactory<>("bonus"));
        table.getColumns().add(bonusCol);

        var typeCol = new TableColumn<AccidentRequest, String>("Тип");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("typeName"));
        table.getColumns().add(typeCol);

        var requestedCol = new TableColumn<AccidentRequest, LocalDateTime>("Прийнято");
        requestedCol.setCellFactory(Tables::dateTimeFormatFactory);
        requestedCol.setCellValueFactory(new PropertyValueFactory<>("datetime"));
        table.getColumns().add(requestedCol);

        var completedCol = new TableColumn<AccidentRequest, LocalDateTime>("Виконано");
        completedCol.setCellFactory(Tables::dateTimeFormatFactory);
        completedCol.setCellValueFactory(new PropertyValueFactory<>("completed"));
        table.getColumns().add(completedCol);

        table.setPrefWidth(575);

        return table;
    }

    private static <T> TableCell<T, LocalDateTime> dateTimeFormatFactory(TableColumn<T, LocalDateTime> column) {
        return new TableCell<>() {
            private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy T hh:mm");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("Ще ні");
                } else {
                    this.setText(format.format(item));
                }
            }
        };
    }



    public static TableView<WorkerBrigadeCnt> workerBrigadeCnt() {
        return new TableBuilder<WorkerBrigadeCnt>()
                .withStringCol("Ім'я", "name")
                .withIntCol("Кількість бригад", "brigadesCnt")
                .build();
    }

//    public static Node dispatcher() {
//    }
}
