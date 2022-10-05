package ua.opu.shveda.databaseprocessor.view;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.structure.NodeElement;
import com.dlsc.formsfx.view.controls.SimpleComboBoxControl;
import com.dlsc.formsfx.view.controls.SimpleDateControl;
import com.dlsc.formsfx.view.controls.SimpleIntegerControl;
import com.dlsc.formsfx.view.controls.SimpleTextControl;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ua.opu.shveda.databaseprocessor.model.*;
import ua.opu.shveda.databaseprocessor.model.exception.BrigadeNotFoundException;
import ua.opu.shveda.databaseprocessor.persistance.*;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.dlsc.formsfx.model.validators.CustomValidator.*;

public class Forms {

    static final BrigadeRepository brigadeRepository = new BrigadeRepository();
    static final WorkShiftRepository workShiftRepository = new WorkShiftRepository();
    static final WorkerRepository workerRepository = new WorkerRepository();
    static final PatientRepository patientRepository = new PatientRepository();
    static final CallRepository callRepository = new CallRepository();

    public static void editUser(User user) {

        SimpleStringProperty newLogin = new SimpleStringProperty(user.getLogin());
        var role = new SimpleObjectProperty<>(User.Role.USER);
        var roles = new SimpleListProperty<>(FXCollections.observableArrayList(List.of(User.Role.values())));

        Form form = Form.of(
                Group.of(
                        Field.ofStringType(newLogin).label("Новий логін")
                                .validate(forPredicate(s -> s.length() > 4, "Логін повинен бути більше 4 символів."))
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
                                .validate(forPredicate(s -> s.length() > 8, "Пароль повинен бути довше 8 сиволів"))
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
                                .validate(forPredicate(s -> s.length() > 4, "Логін повинен бути довше 4 сиволів"))
                                .render(new SimpleTextControl()),
                        Field.ofStringType(password).label("Пароль: ")
                                .required("Ведіть пароль")
                                .validate(forPredicate(s -> s.length() > 8, "Пароль повинен бути довше 8 сиволів"))
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

    public static Optional<Worker> editWorker(Worker w) {
        var name = new SimpleStringProperty(w.getPib());
        var address = new SimpleStringProperty(w.getAddress());
        var phone = new SimpleStringProperty(w.getPhone());
        var birthDate = new SimpleObjectProperty<>(w.getBirthDate());
        var brigadeId = new SimpleIntegerProperty(w.getBrigade() == null ? 0 : w.getBrigade().getId());
        var speciality = new SimpleStringProperty(w.getSpeciality());

        Form form = Form.of(
                Group.of(
                        Field.ofStringType(name).label("Повне ім'я:")
                                .required("Введіть ім'я")
                                .render(new SimpleTextControl()),
                        Field.ofStringType(address).label("Адресса:")
                                .required("Введіть адресу")
                                .render(new SimpleTextControl()),
                        Field.ofStringType(phone).label("Номер телефону")
                                .validate(forPredicate(p -> p.matches("\\d{10}"), "Телефон повинен складатися з десятьох цифр."))
                                .render(new SimpleTextControl()),
                        Field.ofDate(birthDate).label("Дата народження")
                                .render(new SimpleDateControl()),
                        Field.ofIntegerType(brigadeId).label("Ід бригади")
                                        .validate(forPredicate(BrigadeRepository::brigadeExistsById, "Такої бригади не існує")),
                        Field.ofStringType(speciality).label("Посада")
                                .render(new SimpleTextControl())
                )
        );

        if (processCA(form)) {
            Worker worker = new Worker();
            form.persist();
            worker.setId(w.getId());
            worker.setPib(name.get());
            worker.setAddress(address.get());
            worker.setBrigade(brigadeRepository.findById(brigadeId.get()).orElseThrow(BrigadeNotFoundException::new));
            worker.setBirthDate(birthDate.get());
            worker.setPhone(phone.get());
            return Optional.of(worker);
        }

        return Optional.empty();
    }


    public static Optional<Worker> newWorker() {
        return editWorker(new Worker());
    }

    public static Optional<Brigade> editBrigade(Brigade brigade) {
        var ws_id = new SimpleIntegerProperty(brigade.getWorkShift() == null ? 0 : brigade.getWorkShift().getId());
        Form form = Form.of(
                Group.of(
                        Field.ofIntegerType(brigade.numberProperty()).label("Номер:")
                                .required("Введіть номер!")
                                .render(new SimpleIntegerControl()),
                        Field.ofIntegerType(ws_id).label("Ід зміни:")
                                .required("Введіть ід зміни!")
                                .validate(
                                        forPredicate(
                                                WorkShiftRepository::workShiftExistsById,
                                                "Зміни з цим ід не існує"))
                                .render(new SimpleIntegerControl())
                )
        );

        if (processCA(form)) {
            form.persist();
            brigade.setWorkShift(workShiftRepository.findById(ws_id.get()).orElseThrow());
            return Optional.of(brigade);
        }
        return Optional.empty();
    }

    public static Optional<Call> editCall(Call call) {
        var brigade_id = new SimpleIntegerProperty(call.getBrigade() == null ? 0 : call.getBrigade().getId());
        var timeProperty = new SimpleStringProperty(
                call.getTime() == null ? "" :
                call.getTime().format(brigadeRepository.timeFormat));
        Form form = Form.of(
                Group.of(
                        Field.ofDate(call.dateProperty()).label("Дата")
                                .required("Виберіть дату")
                                .render(new SimpleDateControl()),
                        Field.ofStringType(timeProperty).label("Час")
                                .required("Вкажіть час")
                                .validate(forPredicate(time -> time.matches("\\d{2}:\\d{2}"),
                                        "Введіть валідний час в форматі 'HH:MM' "
                                ))
                                .render(new SimpleTextControl()),
                        Field.ofStringType(call.addressProperty()).label("Адреса")
                                .required("Введіть адресу")
                                .render(new SimpleTextControl()),
                        Field.ofIntegerType(brigade_id).label("Ід бригади")
                                .required("Введіть ід бригади")
                                .render(new SimpleIntegerControl())
                )
        );

        if (processCA(form)) {
            form.persist();
            call.setTime(LocalTime.parse(timeProperty.get()));
            call.setBrigade(brigadeRepository.findById(brigade_id.get()).orElseThrow());
            return Optional.of(call);
        }
        return Optional.empty();
    }

    public static Optional<Car> editCar(Car car) {
        var brigadeId = new SimpleIntegerProperty(car.getBrigade() == null ? 0 : car.getBrigade().getId());

        Form form = Form.of(
                Group.of(
                        Field.ofStringType(car.markProperty()).label("Марка")
                                .required("Введіть марку"),
                        Field.ofIntegerType(brigadeId).label("Ід бригади")
                                .required("Введіть ід бригади")
                                .validate(forPredicate(BrigadeRepository::brigadeExistsById,
                                        "Такої бригади нема"))
                )
        );

        if (processCA(form)) {
            form.persist();
            car.setBrigade(brigadeRepository.findById(brigadeId.get()).orElseThrow());
            return Optional.of(car);
        }
        return Optional.empty();
    }

    static final ListProperty<String> states = new SimpleListProperty<>(
            FXCollections.observableArrayList("Легкий", "Тяжкий", "Критичний")
    );

    public static Optional<Diagnose> editDiagnose(Diagnose diagnose) {
        var workerId = new SimpleIntegerProperty(diagnose.getWorker() == null ? 0 : diagnose.getWorker().getId());
        var patientId = new SimpleIntegerProperty(diagnose.getPatient() == null ? 0 : diagnose.getPatient().getId());
        var state = new SimpleObjectProperty<>(states.get(0));

        Form form = Form.of(
                Group.of(
                        Field.ofStringType(diagnose.nameProperty()).label("Назва")
                                .required("Введіть назву"),
                        Field.ofSingleSelectionType(states, state).select(0).label("Стан"),
                        Field.ofIntegerType(workerId).label("Ід лікаря")
                                .required("Введіть ід лікаря")
                                .render(new SimpleIntegerControl()),
                        Field.ofIntegerType(patientId).label("Ід пацієнта")
                                .required("Введіть ід пацієнта")
                                .render(new SimpleIntegerControl())
                )
        );

        if (processCA(form)) {
            form.persist();
            diagnose.setWorker(workerRepository.findById(workerId.get()).orElseThrow());
            diagnose.setPatient(patientRepository.findById(patientId.get()).orElseThrow());
            diagnose.setState(state.get());
            return Optional.of(diagnose);
        }
        return Optional.empty();
    }

    public static Optional<Drug> editDrug(Drug drug) {
        var call = new SimpleIntegerProperty(drug.getCall() == null ? 0 : drug.getCall().getId());
        var price = new SimpleDoubleProperty(drug.priceProperty().doubleValue());

        Form form = Form.of(
                Group.of(
                        Field.ofStringType(drug.nameProperty()).label("Назва")
                                .required("Введіть назву"),
                        Field.ofStringType(drug.dosageProperty()).label("Дозування")
                                .required("Введіть інформацію про дозування"),
                        Field.ofDoubleType(price).label("Ціна")
                                .required("Вкажіть ціну"),
                        Field.ofIntegerType(call).label("ІД виклику")
                                .required("Вкажіть ід виклику")
                                .validate(
                                        forPredicate(CallRepository::callExistsById,
                                        "Виклику з цим ід не існує"))
                )
        );
        if (processCA(form)) {
            form.persist();
            drug.setPrice(price.floatValue());
            drug.setCall(callRepository.findById(call.get()).orElseThrow());
            return Optional.of(drug);
        }
        return Optional.empty();
    }

    public static Optional<Patient> editPatient(Patient patient) {
        Form form = Form.of(
                Group.of(
                        Field.ofStringType(patient.pibProperty()).label("ПІБ")
                                .required("Введіть піб"),
                        Field.ofStringType(patient.sexProperty()).label("Стать")
                                .required("Вкажіть стать")
                                .validate(forPredicate(
                                        sex -> sex.matches("[MW]"),
                                        "Стать може бути чоловіча('M') або жіноча('W')")),
                        Field.ofDate(patient.birthDateProperty()).label("Дата народження"),
                        Field.ofStringType(patient.phoneProperty()).label("Номер телефону")
                                .required("Вкажіть номер телефону")
                                .validate(forPredicate(n -> n.length() >= 10, "телефон повиен містити >= 10"))
                )
        );

        if (processCA(form)) {
            form.persist();
            return Optional.of(patient);
        }
        return Optional.empty();
    }

    public static Optional<WorkShift> editWorkShift(WorkShift ws) {
        var startTimeProperty = new SimpleStringProperty(
                ws.getStartTime() == null ? "" :
                ws.getStartTime().format(brigadeRepository.timeFormat));
        var endTimeProperty = new SimpleStringProperty(
                ws.getEndTime() == null ? "" :
                ws.getEndTime().format(brigadeRepository.timeFormat));

        Form form = Form.of(
                Group.of(
                        Field.ofStringType(startTimeProperty).label("Час початку")
                                .required("Вкажіть час")
                                .validate(forPredicate(time -> time.matches("\\d{2}:\\d{2}"),
                                        "Введіть валідний час в форматі 'HH:MM' "
                                ))
                                .render(new SimpleTextControl()),
                        Field.ofStringType(endTimeProperty).label("Час кінця")
                                .required("Вкажіть час")
                                .validate(forPredicate(time -> time.matches("\\d{2}:\\d{2}"),
                                        "Введіть валідний час в форматі 'HH:MM' "
                                ))
                                .render(new SimpleTextControl()),
                        Field.ofDate(ws.workDateProperty()).label("Дата")
                )
        );

        if (processCA(form)) {
            form.persist();
            ws.setStartTime(LocalTime.parse(startTimeProperty.get()));
            ws.setEndTime(LocalTime.parse(endTimeProperty.get()));
            return Optional.of(ws);
        }
        return Optional.empty();
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

    public record FormResponse<T>(boolean applied, T content) {}

}
