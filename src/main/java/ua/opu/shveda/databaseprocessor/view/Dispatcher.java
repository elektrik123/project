package ua.opu.shveda.databaseprocessor.view;

import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import ua.opu.shveda.databaseprocessor.model.AccidentRequest;
import ua.opu.shveda.databaseprocessor.model.WorkShift;
import ua.opu.shveda.databaseprocessor.persistance.BrigadeRepository;

import java.text.SimpleDateFormat;

public class Dispatcher extends VBox {
    private final Label workShiftLabel;

    private WorkShift workShift;
    private final Accordion freeBrigades;
    private final TableView<AccidentRequest> currentAccidents;

    public Dispatcher() {
        setSpacing(20);
        workShiftLabel = new Label();
        freeBrigades = new Accordion();
        currentAccidents = Tables.accidentTable();
        getChildren().addAll(workShiftLabel, freeBrigades, currentAccidents);
    }

    public Accordion getFreeBrigades() {
        return freeBrigades;
    }

    public Object getCurrentAccidents() {
        return currentAccidents;
    }

    public WorkShift getWorkShift() {
        return workShift;
    }

    void updateBrigades() {
        if (workShift == null) return;
        freeBrigades.getPanes().clear();
        freeBrigades.getPanes().addAll(BrigadeRepository.findForDispatcher(workShift).stream().map(Tables::brigadePane).toList());
    }

    void updateAccidents() {

    }

    public void setWorkShift(WorkShift workShift) {
        this.workShift = workShift;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        workShiftLabel.setText(String.format("Зміна: %s %s", sdf.format(workShift.getDate()), workShift.getDaytime()));
        updateBrigades();
    }
}
