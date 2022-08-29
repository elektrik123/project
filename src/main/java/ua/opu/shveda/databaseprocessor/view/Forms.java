package ua.opu.shveda.databaseprocessor.view;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.structure.NodeElement;
import com.dlsc.formsfx.model.validators.CustomValidator;
import com.dlsc.formsfx.view.controls.SimpleComboBoxControl;
import com.dlsc.formsfx.view.controls.SimpleTextControl;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ua.opu.shveda.databaseprocessor.DBPApp;
import ua.opu.shveda.databaseprocessor.controller.form.WorkerForm;
import ua.opu.shveda.databaseprocessor.model.*;
import ua.opu.shveda.databaseprocessor.persistance.BrigadeRepository;
import ua.opu.shveda.databaseprocessor.persistance.WorkShiftRepository;
import ua.opu.shveda.databaseprocessor.persistance.WorkerRepository;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class Forms {

    public static void editUser(User user) {

        SimpleStringProperty newLogin = new SimpleStringProperty(user.getLogin());
        var role = new SimpleObjectProperty<>(User.Role.USER);
        var roles = new SimpleListProperty<>(FXCollections.observableArrayList(List.of(User.Role.values())));

        Form form = Form.of(
                Group.of(
                        Field.ofStringType(newLogin).label("Новий логін")
                                .validate(CustomValidator.forPredicate(s -> s.length() > 4, "Логін повинен бути більше 4 символів."))
                                .render(new SimpleTextControl()),
                        Field.ofSingleSelectionType(roles, role).label("Роль:")
                                .render(new SimpleComboBoxControl<>())
                )
        );

        if (processCA(form)) {
            form.persist();
            user.setLogin(newLogin.get());
            user.setRole(role.get().toString());
        }
    }

    private static boolean processCA(Form form) {
        Stage stage = new Stage();
        AtomicBoolean applied = new AtomicBoolean(false);
        var accept = new Button("Прийняти");
        var cancel = new Button("Відміна");
        var buttonBox = new HBox(20, accept, cancel);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        form.getGroups().get(0).getElements().add(NodeElement.of(buttonBox));

        cancel.setOnAction(e -> stage.close());
        accept.setOnAction(e -> {
            applied.set(true);
            stage.close();
        });

        form.validProperty().addListener((observableValue, aBoolean, t1) -> accept.setDisable(!t1));
        accept.setDisable(!form.isValid());

        stage.setTitle(form.getTitle());
        var renderer = new FormRenderer(form);
        searchAndSetControlsLabelWidth(renderer, 60);
        stage.setScene(new Scene(renderer));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        return applied.get();
    }

    public static void changePass() {
        var user = Service.getInstance().authentication;
        Stage stage = new Stage();
        AtomicBoolean canceled = new AtomicBoolean(false);

        var oldPassword = new SimpleStringProperty("");
        var newPassword = new SimpleStringProperty("");
        var accept = new Button("Прийняти");
        var cancel = new Button("Відміна");
        var buttonBox = new HBox(20, accept, cancel);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        cancel.setOnAction(e -> {canceled.set(true); stage.close();});


        Form loginForm = Form.of(
                Group.of(
                        Field.ofStringType(oldPassword).label("Старий пароль: ")
                                .required("Ведіть пароль")
                                .render(new SimpleTextControl()),
                        Field.ofStringType(newPassword).label("Новий пароль: ")
                                .required("Ведіть пароль")
                                .validate(CustomValidator.forPredicate(s -> s.length() > 8, "Пароль повинен бути довше 8 сиволів"))
                                .render(new SimpleTextControl()),
                        NodeElement.of(buttonBox)
                )
        );

        accept.setOnAction(e -> {
            loginForm.persist();
            if (!user.password.get().equals(oldPassword.get())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Неправильний пароль!", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            stage.close();
        });

        loginForm.validProperty().addListener((observableValue, aBoolean, t1) -> accept.setDisable(!t1));
        accept.setDisable(true);

        stage.setTitle(user.getLogin() + " новий пароль");
        stage.setScene(new Scene(new FormRenderer(loginForm), 400, 200));
        stage.showAndWait();

        if (canceled.get()) return;
        loginForm.persist();
        user.setPassword(newPassword.get());
        Service.getInstance().updateAuth();
    }

    public static Optional<User> newUser() {
        Stage stage = new Stage();
        AtomicBoolean canceled = new AtomicBoolean(false);

        var login = new SimpleStringProperty("");
        var password = new SimpleStringProperty("");
        var role = new SimpleObjectProperty<>(User.Role.USER);
        var roles = new SimpleListProperty<>(FXCollections.observableArrayList(List.of(User.Role.values())));
        var accept = new Button("Прийняти");
        var cancel = new Button("Відміна");
        var buttonBox = new HBox(20, accept, cancel);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        accept.setOnAction(e -> stage.close());
        cancel.setOnAction(e -> {canceled.set(true); stage.close();});


        Form loginForm = Form.of(
                Group.of(
                        Field.ofStringType(login).label("Логін: ")
                                .required("Ведіть логін")
                                .validate(CustomValidator.forPredicate(s -> s.length() > 4, "Логін повинен бути довше 4 сиволів"))
                                .render(new SimpleTextControl()),
                        Field.ofStringType(password).label("Пароль: ")
                                .required("Ведіть пароль")
                                .validate(CustomValidator.forPredicate(s -> s.length() > 8, "Пароль повинен бути довше 8 сиволів"))
                                .render(new SimpleTextControl()),
                        Field.ofSingleSelectionType(roles, role).label("Роль:")
                                .render(new SimpleComboBoxControl<>()),
                        NodeElement.of(buttonBox)
                )
        );

        loginForm.validProperty().addListener((observableValue, aBoolean, t1) -> accept.setDisable(!t1));
        accept.setDisable(true);

        stage.setTitle("Новий користувач");
        stage.setScene(new Scene(new FormRenderer(loginForm), 400, 300));
        stage.showAndWait();

        if (canceled.get()) return Optional.empty();
        loginForm.persist();
        return Optional.of(new User(login.get(), password.get(), role.get().toString()));
    }

    public static void editWorker(Worker w) {
        var name = new SimpleStringProperty(w.getName());
        var address = new SimpleStringProperty(w.getAddress());
        var phone = new SimpleStringProperty(w.getPhone());
        ObjectProperty<String> post = new SimpleObjectProperty<>(w.getPost());
        ListProperty<String> posts = new SimpleListProperty<>(FXCollections.observableArrayList(Stream.of(Worker.Post.values())
                                .map(p -> p.s)
                                .toList()));

        Form form = Form.of(
                Group.of(
                        Field.ofStringType(name).label("Повне ім'я:")
                                .required("Введіть ім'я")
                                .render(new SimpleTextControl()),
                        Field.ofStringType(address).label("Адресса:")
                                .required("Введіть адресу")
                                .render(new SimpleTextControl()),
                        Field.ofStringType(phone).label("Номер телефону")
                                .validate(CustomValidator.forPredicate(p -> p.matches("\\d{10}"), "Телефон повинен складатися з десятьох цифр."))
                                .render(new SimpleTextControl()),
                        Field.ofSingleSelectionType(posts, post).label("Посада")
                                .render(new SimpleComboBoxControl<>())
                )
        );

        if (processCA(form)) {
            form.persist();
            w.setName(name.get());
            w.setAddress(address.get());
            w.setPhone(phone.get());
            w.setPost(post.get());
        }
    }


    public static Optional<Worker> newWorker() {
        Stage formStage = new Stage();

        var loader = new FXMLLoader(DBPApp.class.getResource("worker-form.fxml"));
        VBox formBox;
        try {
            formBox = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var controller = (WorkerForm) loader.getController();

        controller.apply.setOnAction(e -> formStage.close());
        controller.cancel.setOnAction(e -> formStage.close());

        formStage.setScene(new Scene(formBox));
        formStage.setTitle("Новий робітник");
        formStage.showAndWait();

        return Optional.ofNullable(controller.getWorker());
    }

    public static void searchAndSetControlsLabelWidth(Pane pane, double labelSize) {
        if(pane instanceof GridPane){
            if(pane.getStyleClass().stream().anyMatch(s -> s.contains("simple-"))){
                GridPane gp = (GridPane) pane;
                if (gp.getColumnConstraints().size() == 12) {
                    double rest = 100 - labelSize;
                    for (int i = 0; i < gp.getColumnConstraints().size(); i++) {
                        if (i < 3) {
                            gp.getColumnConstraints().get(i).setPercentWidth(labelSize / 2);
                        }
                        else {
                            gp.getColumnConstraints().get(i).setPercentWidth(rest/10);
                        }
                    }
                }
            }
        }

        for (Node child : pane.getChildren()) {
            if (child instanceof Pane) {
                searchAndSetControlsLabelWidth((Pane) child, labelSize);
            }
        }
    }

    public static List<Integer> selectWorkers(List<Worker> notInBrigade) {
        var table = Tables.workersTable();
        table.setItems(FXCollections.observableList(notInBrigade));
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Form form = Form.of(
                Group.of(NodeElement.of(table))
        );

        form.validProperty().set(true);

        if (processCA(form)) {
            return table.getSelectionModel().getSelectedItems().stream().map(Worker::getId).toList();
        }
        return Collections.emptyList();
    }

    public static <T> List<T> select(TableView<T> table, List<T> content) {
        table.setItems(FXCollections.observableList(content));
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Form form = Form.of(
                Group.of(NodeElement.of(table))
        );

        form.validProperty().set(true);

        if (processCA(form)) {
            return table.getSelectionModel().getSelectedItems().stream().toList();
        }
        return Collections.emptyList();
    }

    public static boolean confirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Підтвердження");
        alert.setHeaderText(message);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(null);
        var opt = alert.showAndWait();
        if (opt.isEmpty()) return false;
        return opt.get() == ButtonType.OK;
    }

    public static void editBrigadeWorkShifts(Brigade brigade) {
        Stage stage = new Stage();
        AtomicBoolean applied = new AtomicBoolean(false);
        Button addWorkShiftButton = new Button("Додати зміну");
        List<WorkShift> toAdd = new ArrayList<>();
        List<WorkShift> toRemove = new ArrayList<>();
        Button removeShiftButton  = new Button("Видалити");

        Button applyButton = new Button("Зберегти зміни");
        Button cancelButton = new Button("Скасувати");

        TableView<WorkShift> wst = Tables.workShiftTable();
        wst.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        wst.setItems(FXCollections.observableList(brigade.workShifts()));

        Form form = Form.of(
                Group.of(
                        NodeElement.of(wst),
                        NodeElement.of(new HBox(40,
                                new HBox(10, addWorkShiftButton, removeShiftButton),
                                new HBox(10, applyButton, cancelButton)
                        ))
                )
        );

        addWorkShiftButton.setOnAction(e -> {
            var selected = select(Tables.workShiftTable(), WorkShiftRepository.findNotInBrigade(brigade));
            toAdd.addAll(selected);
            wst.getItems().addAll(selected);
        });

        removeShiftButton.setOnAction(e -> {
            if (wst.getSelectionModel().getSelectedItems().isEmpty()) return;
            if (confirmation("Ви дійсно хочете прибрати вибрані зміни з бригади?")) {
                toRemove.addAll(wst.getSelectionModel().getSelectedItems());
                wst.getItems().removeAll(wst.getSelectionModel().getSelectedItems());
            }
        });

        applyButton.setOnAction(e -> {applied.set(true); stage.close();});
        cancelButton.setOnAction(e -> stage.close());

        stage.setTitle("Зміни бригади №" + brigade.id());
        stage.setScene(new Scene(new FormRenderer(form)));
        stage.showAndWait();

        if (applied.get()) {
            BrigadeRepository.addWorkShifts(brigade, toAdd);
            BrigadeRepository.removeWorkShifts(brigade, toRemove);
        }
    }

    public static WorkShift selectWorkShift() {
//        var date = new SimpleObjectProperty<>(LocalDate.of(2022, 6, 6));
//        var daytime = new SimpleStringProperty("Денна");
//
//        Form form = Form.of(
//                Group.of(
//                        Field.ofDate(date).label("Дата: ")
//                                .required("Виберіть дату")
//                                .render(new SimpleDateControl()),
//                        Field.ofSingleSelectionType(List.of("Денна", "Нічна")).label("Д/Н")
//                                .render(new SimpleComboBoxControl<>())
//                )
//        );
//
//        if (processCA(form)) {
//            return Optional.ofNullable(WorkShiftRepository.find(date.get(), daytime.get()));
//        } else {
//            return Optional.empty();
//        }

        return select(Tables.workShiftTable(), WorkShiftRepository.findAll()).get(0);
    }

    public static void st1() {
        List<Brigade> list = BrigadeRepository.statement1();
        VBox contentRoot = new VBox();
        ScrollPane sp = new ScrollPane(contentRoot);

        list.forEach(brigade -> contentRoot.getChildren().add(Tables.brigadePane(brigade)));
        Form form = Form.of(
                Group.of(
                        NodeElement.of(sp)
                )
        );

        Stage stage = new Stage();
        stage.setScene(new Scene(new FormRenderer(form), 600, 500));
        stage.showAndWait();

    }

    public static void processInfoInForm(Parent content, String title) {
        Stage stage = new Stage();
        Form form = Form.of(
                Group.of(NodeElement.of(content))
        );
        Scene scene = new Scene(new FormRenderer(form));

        stage.setScene(scene);
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public static void st2() {
        TableView<WorkerRepository.WorkerBrigadeCnt> tw = Tables.workerBrigadeCnt();
        tw.setItems(FXCollections.observableList(WorkerRepository.st2()));
        processInfoInForm(tw, "Навантаження робітників");
    }

    public record FormResponse<T>(boolean applied, T content) {}

    public static void editBrigadeWorkers(Brigade brigade) {
        Stage stage = new Stage();
        AtomicBoolean applied = new AtomicBoolean(false);
        Button addWorkerButton = new Button("Додати робітника");
        List<Worker> toAdd = new ArrayList<>();
        List<Worker> toRemove = new ArrayList<>();
        Button removeWorkerButton = new Button("Видалити");

        Button applyButton = new Button("Зберегти зміни");
        Button cancelButton = new Button("Скасувати");
        TableView<Worker> wt = Tables.workersTable();
        wt.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        wt.setItems(FXCollections.observableList(WorkerRepository.findAllByBrigade(brigade.id())));

        Form form = Form.of(
                Group.of(
                        NodeElement.of(wt),
                        NodeElement.of(new HBox(40,
                                new HBox(10, addWorkerButton, removeWorkerButton),
                                new HBox(10, applyButton, cancelButton)
                        ))
                )
        ).title("Працівник бригади №" + brigade.id());


        addWorkerButton.setOnAction(e -> {
            var notInBrigade = WorkerRepository.findAllByNotInBrigade(brigade.id());
            var selected = select(Tables.workersTable(), notInBrigade);
            wt.getItems().addAll(selected);
            toAdd.addAll(selected);
        });

        removeWorkerButton.setOnAction(e -> {
            if (wt.getSelectionModel().getSelectedItems().isEmpty()) return;
            if (confirmation("Ви дійсно хочете прибрати вибраних працівників з бригади?")) {
                toRemove.addAll(wt.getSelectionModel().getSelectedItems());
                wt.getItems().removeAll(wt.getSelectionModel().getSelectedItems());
            }
        });

        applyButton.setOnAction(e -> {applied.set(true); stage.close();});
        cancelButton.setOnAction(e -> stage.close());


        stage.setTitle(form.getTitle());
        stage.setScene(new Scene(new FormRenderer(form)));
        stage.showAndWait();

        if (applied.get()) {
            BrigadeRepository.addWorkers(brigade, toAdd);
            BrigadeRepository.removeWorkers(brigade, toRemove);
        }
    }
}
