package ua.opu.shveda.databaseprocessor.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.opu.shveda.databaseprocessor.model.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;


public class Tables {
    private static class TableBuilder<T> {
        private final TableView<T> table;

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
            var col = new TableColumn<T, Integer>(name);
            col.setCellValueFactory(new PropertyValueFactory<>(property));
            table.getColumns().add(col);
            return this;
        }

        TableBuilder<T> withObjectFieldCol(String name, Function<T, String> getter) {
            var col = new TableColumn<T, String>(name);
            col.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
            table.getColumns().add(col);
            return this;
        }

        TableBuilder<T> withWidth() {
            table.setPrefWidth(575);
            return this;
        }

        TableBuilder<T> withDateCol(String name, String property) {
            var col = new TableColumn<T, LocalDate>(name);
            col.setCellFactory(Tables::dateFormatFactory);
            col.setCellValueFactory(new PropertyValueFactory<>(property));
            table.getColumns().add(col);
            return this;
        }

        TableBuilder<T> withTimeCol(String name, String property) {
            var col = new TableColumn<T, LocalTime>(name);
            col.setCellFactory(Tables::timeFormatFactory);
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

    public static TableView<User> usersTable() {
        return new TableBuilder<User>()
                .withStringCol("??????????", "login")
                .withStringCol("????????", "role")
                .build();
    }

    public static TableView<Worker> workersTable() {
        return new TableBuilder<Worker>()
                .withIntCol("Id", "id")
                .withStringCol("??????", "pib")
                .withStringCol("????????????", "address")
                .withStringCol("??????????????", "phone")
                .withDateCol("???????? ????????????????????", "birthDate")
                .withObjectFieldCol("??????????????", worker -> String.valueOf(worker.getBrigade().getId()))
                .withStringCol("??????????????????????????", "speciality")
                .withWidth()
                .build();
    }

    public static TableView<Brigade> brigadeTable() {
        return new TableBuilder<Brigade>()
                .withIntCol("Id", "id")
                .withIntCol("??????????", "number")
                .withObjectFieldCol("??????????????", brigade -> String.valueOf(brigade.getWorkShift().getId()))
                .withWidth()
                .build();
    }

    public static TableView<Call> callTable() {
        return new TableBuilder<Call>()
                .withIntCol("Id", "id")
                .withDateCol("????????", "date")
                .withTimeCol("??????", "time")
                .withStringCol("????????????", "address")
                .withObjectFieldCol("??????????????", call -> String.valueOf(call.getBrigade().getId()))
                .withWidth()
                .build();
    }

    public static TableView<Car> carTable() {
        return new TableBuilder<Car>()
                .withIntCol("Id", "id")
                .withStringCol("??????????", "mark")
                .withObjectFieldCol("??????????????", car -> String.valueOf(car.getBrigade().getId()))
                .withWidth()
                .build();
    }

    public static TableView<Diagnose> diagnoseTable() {
        return new TableBuilder<Diagnose>()
                .withIntCol("Id", "id")
                .withObjectFieldCol("??????????????", diagnose -> String.valueOf(diagnose.getPatient().getPib()))
                .withStringCol("??????????", "name")
                .withStringCol("????????", "state")
                .withObjectFieldCol("??????????, ???? ??????????", diagnose -> String.valueOf(diagnose.getWorker().getPib()))
                .withWidth()
                .build();
    }

    public static TableView<Drug> drugTable() {
        return new TableBuilder<Drug>()
                .withIntCol("Id", "id")
                .withStringCol("??????????", "name")
                .withStringCol("??????????????????", "dosage")
                .withIntCol("????????", "price")
                .withObjectFieldCol("????????????", drug -> String.valueOf(drug.getCall().getId()))
                .withWidth()
                .build();
    }

    public static TableView<Patient> patientTable() {
        return new TableBuilder<Patient>()
                .withIntCol("Id", "id")
                .withStringCol("??????", "pib")
                .withStringCol("??????????", "sex")
                .withDateCol("???????? ????????????????????", "birthDate")
                .withStringCol("??????????????", "phone")
                .withWidth()
                .build();
    }

    public static TableView<WorkShift> workShiftTable() {
        return new TableBuilder<WorkShift>()
                .withIntCol("Id", "id")
                .withTimeCol("?????? ??????????????", "startTime")
                .withTimeCol("?????? ??????????", "endTime")
                .withDateCol("????????", "workDate")
                .withWidth()
                .build();
    }

    private static <T> TableCell<T, LocalDate> dateFormatFactory(TableColumn<T, LocalDate> column) {
        return new TableCell<>() {
            private final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    this.setText(format.format(item));

                }
            }
        };
    }

    private static <T> TableCell<T, LocalTime> timeFormatFactory(TableColumn<T, LocalTime> column) {
        return new TableCell<>() {
            private final DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mm:ss");

            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    this.setText(format.format(item));

                }
            }
        };
    }

    private static <T> TableCell<T, LocalDateTime> dateTimeFormatFactory(TableColumn<T, LocalDateTime> column) {
        return new TableCell<>() {
            private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy T hh:mm");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("???? ????");
                } else {
                    this.setText(format.format(item));
                }
            }
        };
    }


}
