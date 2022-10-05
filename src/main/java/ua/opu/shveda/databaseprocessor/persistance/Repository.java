package ua.opu.shveda.databaseprocessor.persistance;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    String dbPath = "jdbc:sqlite:C:\\Users\\сашка\\IdeaProjects\\db-curse-project\\medicine_base.db";

    Optional<T> findById(int id);
    List<T> findAll();
    void update(T t);
    void insert(T t);
}
