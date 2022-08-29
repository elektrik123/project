package ua.opu.shveda.databaseprocessor.model;

import ua.opu.shveda.databaseprocessor.persistance.WorkerRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Mappers {
    public static Worker mapWorker(ResultSet resultSet) throws SQLException {
        var id = resultSet.getInt("id");
        var name = resultSet.getString("fullname");
        var address = resultSet.getString("address");
        var phone = resultSet.getString("phone_number");
        var post = resultSet.getString("post");
        return new Worker(id, name, address, phone, post);
    }

    public static User mapUser(ResultSet set) throws SQLException {
        var login = set.getString("login");
        var password = set.getString("password");
        var role = set.getString("role");
        return new User(login, password, role);
    }

    public static Brigade mapBrigade(ResultSet set) throws SQLException {
        return new Brigade(set.getInt("id_number"),
                new ArrayList<>(), new ArrayList<>()
        );
    }

    public static WorkShift mapWorkShift(ResultSet set) throws SQLException, ParseException {
        return new WorkShift(set.getInt("id"),
                new SimpleDateFormat("yyyy-MM-dd").parse(set.getString("work_date")),
                set.getString("daytime")
        );
    }

    public static WorkerRepository.WorkerBrigadeCnt mapWBCnt(ResultSet set) throws SQLException {
        return new WorkerRepository.WorkerBrigadeCnt(set.getString("name"), set.getInt("b_count"));
    }

    public static AccidentType mapAccidentType(ResultSet rs) throws SQLException {
        return new AccidentType(rs.getInt("id"), rs.getString("name"));
    }
}
