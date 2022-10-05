package ua.opu.shveda.databaseprocessor.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ua.opu.shveda.databaseprocessor.DBPApp;
import ua.opu.shveda.databaseprocessor.model.*;
import ua.opu.shveda.databaseprocessor.persistance.*;
import ua.opu.shveda.databaseprocessor.view.Forms;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static ua.opu.shveda.databaseprocessor.view.Tables.*;

public class MainView extends VBox {

    @FXML ScrollPane workerTableContainer;
    @FXML BorderPane workerWorkspace;
    @FXML ScrollPane userTableContainer;
    @FXML Button newWorkerButton;
    @FXML Button editWorkerButton;
    @FXML Button deleteWorkerButton;
    @FXML Button newUserButton;
    @FXML Button editUserButton;
    @FXML Button editBWorkersButton;
    @FXML Button editBWSsButton;


    @FXML BorderPane dispatcherWorkSpace;
    @FXML Button editWorkShift;





    @FXML ScrollPane workShiftsRoot;
    @FXML Tab userTab;
    @FXML Tab dispatcherTab;

    final Stage thisStage;
    final Stage loginStage;



    @FXML MenuItem changePass;
    @FXML MenuItem exitFromSign;

    @FXML MenuItem statement1;
    @FXML MenuItem statement2;

    private final Service service = Service.getInstance();

    //Table Containers
    @FXML ScrollPane brigadeTableContainer;
    @FXML ScrollPane callTableContainer;
    @FXML ScrollPane carTableContainer;
    @FXML ScrollPane diagnoseTableContainer;
    @FXML ScrollPane drugTableContainer;
    @FXML ScrollPane patientTableContainer;
    @FXML ScrollPane workShiftTableContainer;

    //EditButtons
    @FXML Button editBrigadeButton;
    @FXML Button editCallButton;
    @FXML Button editCarButton;
    @FXML Button editDiagnoseButton;
    @FXML Button editDrugButton;
    @FXML Button editPatientButton;
    @FXML Button editWorkShiftButton;

    //New buttons
    @FXML Button newBrigadeButton;
    @FXML Button newCallButton;
    @FXML Button newCarButton;
    @FXML Button newDiagnoseButton;
    @FXML Button newDrugButton;
    @FXML Button newPatientButton;
    @FXML Button newWorkShiftButton;

    //Tables
    private final TableView<Worker> workerTable;
    private final TableView<User> userTable;
    private final TableView<Brigade> brigadeTable;
    private final TableView<Call> callTable;
    private final TableView<Car> carTable;
    private final TableView<Diagnose> diagnoseTable;
    private final TableView<Drug> drugTable;
    private final TableView<Patient> patientTable;
    private final TableView<WorkShift> workShiftTable;

    static Action updateAction;


    //Repositories
    final WorkerRepository workerRepository = new WorkerRepository();
    final BrigadeRepository brigadeRepository = new BrigadeRepository();
    final CallRepository callRepository = new CallRepository();
    final CarRepository carRepository = new CarRepository();
    final DiagnoseRepository diagnoseRepository = new DiagnoseRepository();
    final DrugRepository drugRepository = new DrugRepository();
    final PatientRepository patientRepository = new PatientRepository();
    final WorkShiftRepository workShiftRepository = new WorkShiftRepository();

    @FXML public void initialize() {

        initUser();

        initWorker();

        brigadeTableContainer.setContent(brigadeTable);
        callTableContainer.setContent(callTable);
        carTableContainer.setContent(carTable);
        diagnoseTableContainer.setContent(diagnoseTable);
        drugTableContainer.setContent(drugTable);
        patientTableContainer.setContent(patientTable);
        workShiftTableContainer.setContent(workShiftTable);

        editBrigadeButton.setDisable(true);
        brigadeTable.getSelectionModel().selectedItemProperty().addListener((observableValue, user, t1) ->
                editBrigadeButton.setDisable(t1 == null));


       setupButtons();
        updateAction = this::updateTables;

        updateTables();
    }
    private void setupButtons(){
        initEditMechanicFor(
                brigadeRepository, editBrigadeButton, newBrigadeButton,
                Brigade::new, Forms::editBrigade, brigadeTable
        );
        initEditMechanicFor(
                callRepository, editCallButton, newCallButton,
                Call::new, Forms::editCall, callTable
        );
        initEditMechanicFor(
                carRepository, editCarButton, newCarButton,
                Car::new, Forms::editCar, carTable
        );
        initEditMechanicFor(
                diagnoseRepository, editDiagnoseButton, newDiagnoseButton,
                Diagnose::new, Forms::editDiagnose, diagnoseTable
        );
        initEditMechanicFor(
                drugRepository, editDrugButton, newDrugButton,
                Drug::new, Forms::editDrug, drugTable
        );
        initEditMechanicFor(
                patientRepository, editPatientButton, newPatientButton,
                Patient::new, Forms::editPatient, patientTable
        );
        initEditMechanicFor(
                workShiftRepository, editWorkShiftButton, newWorkShiftButton,
                WorkShift::new, Forms::editWorkShift, workShiftTable
        );


    }
    static <T> void initEditMechanicFor(
            Repository<T> repository,
            Button editButton, Button newButton,
            Supplier<T> constructor, Function<T, Optional<T>> form,
            TableView<T> table
    ) {
        editButton.setDisable(true);
        table.getSelectionModel().selectedItemProperty().addListener((observableValue, user, t1) ->
                editButton.setDisable(t1 == null));

        editButton.setOnAction(event -> {
            var t = table.getSelectionModel().getSelectedItem();
            var optional = form.apply(t);
            optional.ifPresent(repository::update);
        });

        newButton.setOnAction(e -> {
            form.apply(constructor.get()).ifPresent(repository::insert);
            updateAction.execute();
        });
    }

    void updateTables() {
        brigadeTable.setItems(FXCollections.observableArrayList(brigadeRepository.findAll()));
        callTable.setItems(FXCollections.observableArrayList(callRepository.findAll()));
        carTable.setItems(FXCollections.observableArrayList(carRepository.findAll()));
        diagnoseTable.setItems(FXCollections.observableArrayList(diagnoseRepository.findAll()));
        drugTable.setItems(FXCollections.observableArrayList(drugRepository.findAll()));
        patientTable.setItems(FXCollections.observableArrayList(patientRepository.findAll()));
        workShiftTable.setItems(FXCollections.observableArrayList(workShiftRepository.findAll()));
    }

    private void initUser() {
        userTable.setItems(service.getUsers());
        userTableContainer.setContent(userTable);

        editUserButton.setOnAction(e -> {
            var user = userTable.getSelectionModel().getSelectedItem();
            var oldLogin = user.getLogin();
            Forms.editUser(user);
            UserRepository.updateUser(user, oldLogin);
            userTable.setItems(UserRepository.findAllObservable());
        });

        newUserButton.setOnAction(e -> {
            var user = Forms.newUser();
            user.ifPresent(u -> {
                service.addUser(u);
                userTable.setItems(service.getUsers());
            });
        });
    }

    private void initWorker() {
        workerTable.setItems(service.getWorkers());
        workerTableContainer.setContent(workerTable);

        newWorkerButton.setOnAction(event -> {
            Optional<Worker> workerOptional = Forms.newWorker();
            workerOptional.ifPresent(service::addWorker);
        });

        editWorkerButton.setDisable(true);
        workerTable.getSelectionModel().selectedItemProperty().addListener((observableValue, user, t1) ->
                editWorkerButton.setDisable(t1 == null));

        editWorkerButton.setOnAction(e -> {
            var worker = workerTable.getSelectionModel().getSelectedItem();
            var workerOptional = Forms.editWorker(worker);
            workerOptional.ifPresent(workerRepository::update);
            workerTable.setItems(service.getWorkers());
        });
    }

    public MainView(Stage mainStage, Stage loginStage) throws IOException {
        workerTable = workersTable();
        userTable = usersTable();
        brigadeTable = brigadeTable();
        callTable = callTable();
        carTable = carTable();
        diagnoseTable = diagnoseTable();
        drugTable = drugTable();
        patientTable = patientTable();
        workShiftTable = workShiftTable();


        thisStage = mainStage;
        this.loginStage = loginStage;

        FXMLLoader loader = new FXMLLoader(DBPApp.class.getResource("main-view.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }
}
