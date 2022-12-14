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
                        Field.ofStringType(newLogin).label("?????????? ??????????")
                                .validate(forPredicate(s -> s.length() > 4, "?????????? ?????????????? ???????? ???????????? 4 ????????????????."))
                                .render(new SimpleTextControl()),
                        Field.ofSingleSelectionType(roles, role).label("????????:")
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
        var accept = new Button("????????????????");
        var cancel = new Button("??????????????");
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
        var accept = new Button("????????????????");
        var cancel = new Button("??????????????");
        var buttonBox = new HBox(20, accept, cancel);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        cancel.setOnAction(e -> {canceled.set(true); stage.close();});


        Form loginForm = Form.of(
                Group.of(
                        Field.ofStringType(oldPassword).label("???????????? ????????????: ")
                                .required("???????????? ????????????")
                                .render(new SimpleTextControl()),
                        Field.ofStringType(newPassword).label("?????????? ????????????: ")
                                .required("???????????? ????????????")
                                .validate(forPredicate(s -> s.length() > 8, "???????????? ?????????????? ???????? ?????????? 8 ??????????????"))
                                .render(new SimpleTextControl()),
                        NodeElement.of(buttonBox)
                )
        );

        accept.setOnAction(e -> {
            loginForm.persist();
            if (!user.password.get().equals(oldPassword.get())) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "???????????????????????? ????????????!", ButtonType.OK);
                alert.showAndWait();
                return;
            }
            stage.close();
        });

        loginForm.validProperty().addListener((observableValue, aBoolean, t1) -> accept.setDisable(!t1));
        accept.setDisable(true);

        stage.setTitle(user.getLogin() + " ?????????? ????????????");
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
        var accept = new Button("????????????????");
        var cancel = new Button("??????????????");
        var buttonBox = new HBox(20, accept, cancel);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        accept.setOnAction(e -> stage.close());
        cancel.setOnAction(e -> {canceled.set(true); stage.close();});


        Form loginForm = Form.of(
                Group.of(
                        Field.ofStringType(login).label("??????????: ")
                                .required("???????????? ??????????")
                                .validate(forPredicate(s -> s.length() > 4, "?????????? ?????????????? ???????? ?????????? 4 ??????????????"))
                                .render(new SimpleTextControl()),
                        Field.ofStringType(password).label("????????????: ")
                                .required("???????????? ????????????")
                                .validate(forPredicate(s -> s.length() > 8, "???????????? ?????????????? ???????? ?????????? 8 ??????????????"))
                                .render(new SimpleTextControl()),
                        Field.ofSingleSelectionType(roles, role).label("????????:")
                                .render(new SimpleComboBoxControl<>()),
                        NodeElement.of(buttonBox)
                )
        );

        loginForm.validProperty().addListener((observableValue, aBoolean, t1) -> accept.setDisable(!t1));
        accept.setDisable(true);

        stage.setTitle("?????????? ????????????????????");
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
                        Field.ofStringType(name).label("?????????? ????'??:")
                                .required("?????????????? ????'??")
                                .render(new SimpleTextControl()),
                        Field.ofStringType(address).label("??????????????:")
                                .required("?????????????? ????????????")
                                .render(new SimpleTextControl()),
                        Field.ofStringType(phone).label("?????????? ????????????????")
                                .validate(forPredicate(p -> p.matches("\\d{10}"), "?????????????? ?????????????? ???????????????????? ?? ???????????????? ????????."))
                                .render(new SimpleTextControl()),
                        Field.ofDate(birthDate).label("???????? ????????????????????")
                                .render(new SimpleDateControl()),
                        Field.ofIntegerType(brigadeId).label("???? ??????????????")
                                        .validate(forPredicate(BrigadeRepository::brigadeExistsById, "?????????? ?????????????? ???? ??????????")),
                        Field.ofStringType(speciality).label("????????????")
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
                        Field.ofIntegerType(brigade.numberProperty()).label("??????????:")
                                .required("?????????????? ??????????!")
                                .render(new SimpleIntegerControl()),
                        Field.ofIntegerType(ws_id).label("???? ??????????:")
                                .required("?????????????? ???? ??????????!")
                                .validate(
                                        forPredicate(
                                                WorkShiftRepository::workShiftExistsById,
                                                "?????????? ?? ?????? ???? ???? ??????????"))
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
                        Field.ofDate(call.dateProperty()).label("????????")
                                .required("???????????????? ????????")
                                .render(new SimpleDateControl()),
                        Field.ofStringType(timeProperty).label("??????")
                                .required("?????????????? ??????")
                                .validate(forPredicate(time -> time.matches("\\d{2}:\\d{2}"),
                                        "?????????????? ???????????????? ?????? ?? ?????????????? 'HH:MM' "
                                ))
                                .render(new SimpleTextControl()),
                        Field.ofStringType(call.addressProperty()).label("????????????")
                                .required("?????????????? ????????????")
                                .render(new SimpleTextControl()),
                        Field.ofIntegerType(brigade_id).label("???? ??????????????")
                                .required("?????????????? ???? ??????????????")
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
                        Field.ofStringType(car.markProperty()).label("??????????")
                                .required("?????????????? ??????????"),
                        Field.ofIntegerType(brigadeId).label("???? ??????????????")
                                .required("?????????????? ???? ??????????????")
                                .validate(forPredicate(BrigadeRepository::brigadeExistsById,
                                        "?????????? ?????????????? ????????"))
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
            FXCollections.observableArrayList("????????????", "????????????", "??????????????????")
    );

    public static Optional<Diagnose> editDiagnose(Diagnose diagnose) {
        var workerId = new SimpleIntegerProperty(diagnose.getWorker() == null ? 0 : diagnose.getWorker().getId());
        var patientId = new SimpleIntegerProperty(diagnose.getPatient() == null ? 0 : diagnose.getPatient().getId());
        var state = new SimpleObjectProperty<>(states.get(0));

        Form form = Form.of(
                Group.of(
                        Field.ofStringType(diagnose.nameProperty()).label("??????????")
                                .required("?????????????? ??????????"),
                        Field.ofSingleSelectionType(states, state).select(0).label("????????"),
                        Field.ofIntegerType(workerId).label("???? ????????????")
                                .required("?????????????? ???? ????????????")
                                .render(new SimpleIntegerControl()),
                        Field.ofIntegerType(patientId).label("???? ????????????????")
                                .required("?????????????? ???? ????????????????")
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
                        Field.ofStringType(drug.nameProperty()).label("??????????")
                                .required("?????????????? ??????????"),
                        Field.ofStringType(drug.dosageProperty()).label("??????????????????")
                                .required("?????????????? ???????????????????? ?????? ??????????????????"),
                        Field.ofDoubleType(price).label("????????")
                                .required("?????????????? ????????"),
                        Field.ofIntegerType(call).label("???? ??????????????")
                                .required("?????????????? ???? ??????????????")
                                .validate(
                                        forPredicate(CallRepository::callExistsById,
                                        "?????????????? ?? ?????? ???? ???? ??????????"))
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
                        Field.ofStringType(patient.pibProperty()).label("??????")
                                .required("?????????????? ??????"),
                        Field.ofStringType(patient.sexProperty()).label("??????????")
                                .required("?????????????? ??????????")
                                .validate(forPredicate(
                                        sex -> sex.matches("[MW]"),
                                        "?????????? ???????? ???????? ????????????????('M') ?????? ????????????('W')")),
                        Field.ofDate(patient.birthDateProperty()).label("???????? ????????????????????"),
                        Field.ofStringType(patient.phoneProperty()).label("?????????? ????????????????")
                                .required("?????????????? ?????????? ????????????????")
                                .validate(forPredicate(n -> n.length() >= 10, "?????????????? ???????????? ?????????????? >= 10"))
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
                        Field.ofStringType(startTimeProperty).label("?????? ??????????????")
                                .required("?????????????? ??????")
                                .validate(forPredicate(time -> time.matches("\\d{2}:\\d{2}"),
                                        "?????????????? ???????????????? ?????? ?? ?????????????? 'HH:MM' "
                                ))
                                .render(new SimpleTextControl()),
                        Field.ofStringType(endTimeProperty).label("?????? ??????????")
                                .required("?????????????? ??????")
                                .validate(forPredicate(time -> time.matches("\\d{2}:\\d{2}"),
                                        "?????????????? ???????????????? ?????? ?? ?????????????? 'HH:MM' "
                                ))
                                .render(new SimpleTextControl()),
                        Field.ofDate(ws.workDateProperty()).label("????????")
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
        alert.setTitle("??????????????????????????");
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
