package ua.opu.shveda.databaseprocessor.model;

import java.util.Date;
import java.util.List;

public record Brigade(int id, List<WorkShift> workShifts, List<Worker> workers) {
}
