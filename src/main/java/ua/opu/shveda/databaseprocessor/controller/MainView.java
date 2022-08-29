package ua.opu.shveda.databaseprocessor.controller;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.structure.NodeElement;
import com.dlsc.formsfx.view.controls.SimpleComboBoxControl;
import com.dlsc.formsfx.view.controls.SimpleTextControl;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ua.opu.shveda.databaseprocessor.DBPApp;
import ua.opu.shveda.databaseprocessor.model.*;
import ua.opu.shveda.databaseprocessor.persistance.BrigadeRepository;
import ua.opu.shveda.databaseprocessor.persistance.UserRepository;
import ua.opu.shveda.databaseprocessor.persistance.WorkerRepository;
import ua.opu.shveda.databaseprocessor.view.Dispatcher;
import ua.opu.shveda.databaseprocessor.view.Forms;
import ua.opu.shveda.databaseprocessor.view.Tables;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static ua.opu.shveda.databaseprocessor.view.Forms.*;

public class MainView extends VBox {

    @FXML ScrollPane workerTableContainer;
    @FXML BorderPane workerWorkspace;
    @FXML ScrollPane userTableContainer;
    @FXML Button newWorkerButton;
    @FXML Button editWorkerButton;
    @FXML Button deleteWorkerButton;
    @FXML Button newUserButton;
    @FXML Button editUserButton;

    @FXML Accordion brigadesAccordion;
    @FXML Button editBWorkersButton;
    @FXML Button editBWSsButton;


    @FXML BorderPane dispatcherWorkSpace;
    @FXML Button editWorkShift;





    @FXML ScrollPane workShiftsRoot;
    @FXML Tab userTab;
    @FXML Tab dispatcherTab;

    Stage thisStage;
    Stage loginStage;



    @FXML MenuItem changePass;
    @FXML MenuItem exitFromSign;

    @FXML MenuItem statement1;
    @FXML MenuItem statement2;

    private final Service service = Service.getInstance();
    private final TableView<Worker> workerTable;
    private final TableView<User> userTable;
    private final TableView<WorkShift> workShiftTable;

    @FXML public void initialize() {
        workerTableContainer.setContent(workerTable);
        workerTable.setItems(service.getWorkers());
        userTableContainer.setContent(userTable);
        userTable.setItems(service.getUsers());
        updateBrigades();
        Dispatcher dispatcher = new Dispatcher();
        dispatcherWorkSpace.setCenter(dispatcher);
        workShiftsRoot.setContent(workShiftTable);
        userTab.setDisable(!"ADMIN".equals(Service.getInstance().authentication.role.get()));
        userTab.setDisable(!List.of("DISPATCHER", "ADMIN").contains(Service.getInstance().authentication.role.get()));

        newWorkerButton.setOnAction(e -> {
            var worker = Forms.newWorker();
            worker.ifPresent(w -> {
                service.addWorker(w);
                workerTable.setItems(service.getWorkers());
            });
        });

        newUserButton.setOnAction(e -> {
            var user = Forms.newUser();
            user.ifPresent(u -> {
                service.addUser(u);
                userTable.setItems(service.getUsers());
            });
        });

        changePass.setOnAction(e -> changePass());

        editUserButton.setDisable(true);
        userTable.getSelectionModel().selectedItemProperty().addListener((observableValue, user, t1) ->
                editUserButton.setDisable(t1 == null));
        editUserButton.setOnAction(e -> {
            var user = userTable.getSelectionModel().getSelectedItem();
            var oldLogin = user.getLogin();
            editUser(user);
            UserRepository.update(user, oldLogin);
            userTable.setItems(UserRepository.findAll());
        });

        editWorkerButton.setDisable(true);
        workerTable.getSelectionModel().selectedItemProperty().addListener((observableValue, user, t1) ->
                editWorkerButton.setDisable(t1 == null));

        editWorkerButton.setOnAction(e -> {
            var worker = workerTable.getSelectionModel().getSelectedItem();
            editWorker(worker);
            WorkerRepository.update(worker);
            workerTable.setItems(WorkerRepository.findAll());
        });

        deleteWorkerButton.setOnAction(e -> {
            var worker = workerTable.getSelectionModel().getSelectedItem();
            if (Forms.confirmation("Ви певні що хочете видалити запис про робітника " + worker.getName() + "?")) WorkerRepository.delete(worker);
            workerTable.setItems(WorkerRepository.findAll());
        });

        editBWorkersButton.setOnAction(e -> {
            var number = Integer.parseInt(((Label) brigadesAccordion.getExpandedPane().getGraphic()).getText());
            Brigade brigade = BrigadeRepository.findByNumber(number).orElseThrow(RuntimeException::new);
            Forms.editBrigadeWorkers(brigade);
            updateBrigades();
        });

        editBWSsButton.setOnAction(e -> {
            var number  = Integer.parseInt(((Label) brigadesAccordion.getExpandedPane().getGraphic()).getText());
            Brigade brigade = BrigadeRepository.findByNumber(number).orElseThrow();
            Forms.editBrigadeWorkShifts(brigade);
            updateBrigades();
        });

        editWorkShift.setOnAction(e -> {
            var newWS = Forms.selectWorkShift();
            dispatcher.setWorkShift(newWS);
        });

        exitFromSign.setOnAction(e -> {
            thisStage.close();
            loginStage.show();
        });

        statement1.setOnAction(e -> {
            Forms.st1();
        });

        statement2.setOnAction(e -> Forms.st2());

        initFilter();
    }

    private void updateBrigades() {
        brigadesAccordion.getPanes().clear();
        brigadesAccordion.getPanes().addAll(BrigadeRepository.findAll().stream().map(Tables::brigadePane).toList());
    }

    private void initFilter() {
        Button filterWorker = new Button("Застосувати фільтр");
        Button dropFilter = new Button("Скасувати фільтр");
        Worker wf = new Worker(-1, "", "", "", "");

        ObjectProperty<String> post = new SimpleObjectProperty<>("Бригадир");
        var postStream = Stream.concat(Stream.of(Worker.Post.values())
                .map(p -> p.s), Stream.of("Будь яка"));
        ListProperty<String> posts = new SimpleListProperty<>(FXCollections.observableArrayList(postStream.toList()));

        Form workerFilter =  Form.of(
                Group.of(
                        NodeElement.of(new Label("Фільтр")),
                        Field.ofStringType(wf.nameProperty()).label("Повне ім'я:").render(new SimpleTextControl()),
                        Field.ofStringType(wf.addressProperty()).label("Адресса:").render(new SimpleTextControl()),
                        Field.ofStringType(wf.phoneProperty()).label("Номер телефону").render(new SimpleTextControl()),
                        Field.ofSingleSelectionType(posts, post).label("Посада").render(new SimpleComboBoxControl<>()),
                        NodeElement.of(new HBox(10, filterWorker, dropFilter))
                )
        );
        var wfRenderer = new FormRenderer(workerFilter);
        Forms.searchAndSetControlsLabelWidth(wfRenderer, 50);
        workerWorkspace.setRight(wfRenderer);

        filterWorker.setOnAction(e -> {
            workerFilter.persist();
            wf.setPost("Будь яка".equals(post.get()) ? "" : post.get());
            List<Worker> list = WorkerRepository.findAllFilter(wf);
            workerTable.setItems(FXCollections.observableList(list));
        });

        dropFilter.setOnAction(e -> workerTable.setItems(WorkerRepository.findAll()));

    }

    public MainView(Stage mainStage, Stage loginStage) throws IOException {
        workerTable = Tables.workersTable();
        userTable = Tables.usersTable();
        workShiftTable = Tables.workShiftTable();


        thisStage = mainStage;
        this.loginStage = loginStage;

        FXMLLoader loader = new FXMLLoader(DBPApp.class.getResource("main-view.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }
}
