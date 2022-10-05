package ua.opu.shveda.databaseprocessor.model;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ua.opu.shveda.databaseprocessor.persistance.UserRepository;
import ua.opu.shveda.databaseprocessor.persistance.WorkerRepository;

public class Service {
    private static Service instance;
    private final WorkerRepository workerRepository = new WorkerRepository();
    private final UserRepository userRepository = new UserRepository();

    public User authentication;

    private Service() {
    }

    public static Service getInstance() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }


    public ObservableList<Worker> getWorkers() {
        return FXCollections.observableArrayList(workerRepository.findAll());
    }

    public void addWorker(Worker worker) {
        workerRepository.insert(worker);
    }

    public ObservableList<User> getUsers() {
        return UserRepository.findAllObservable();
    }

    public User getUser(String  login) {
        return UserRepository.findByLogin(login);
    }

    public boolean authenticateUser(String login, String password) {
        return UserRepository.validate(login, password);
    }

    public void addUser(User user) {
        UserRepository.create(user);
    }

    public void updateAuth() {
        UserRepository.updateUser(authentication);
    }
}
