package ua.opu.shveda.databaseprocessor.model;

import java.time.LocalDateTime;

public record Request(int id, LocalDateTime dateTime, String address)  {
}
