package ua.opu.shveda.databaseprocessor.model;


import javafx.collections.ObservableList;
import ua.opu.shveda.databaseprocessor.persistance.UserRepository;
import ua.opu.shveda.databaseprocessor.persistance.WorkerRepository;

import java.util.Optional;

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
        return workerRepository.findAll();
    }

    public void addWorker(Worker worker) {
        workerRepository.create(worker);
    }

    public ObservableList<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(String  login) {
        return userRepository.findByLogin(login);
    }

    public boolean authenticateUser(String login, String password) {
        return userRepository.validate(login, password);
    }

    public void addUser(User user) {
        userRepository.create(user);
    }

    public void updateAuth() {
        userRepository.update(authentication);
    }
}
