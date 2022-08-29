package ua.opu.shveda.databaseprocessor.controller.form;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import net.synedra.validatorfx.TooltipWrapper;
import net.synedra.validatorfx.Validator;
import ua.opu.shveda.databaseprocessor.model.Worker;

public class WorkerForm {
    private Validator validator = new Validator();

    public TextField nameField;
    public TextField addressField;
    public TextField phoneField;
    public TextField postField;
    public Button apply;
    public Button cancel;

    public void initialize() {
        validator.createCheck()
                .dependsOn("phone", phoneField.textProperty())
                .withMethod(c -> {
                    String phone = c.get("phone");
                    if (!phone.matches("\\d{10}")) {
                        c.error("Телефон повинен складатися з десятьох цифр.");
                    }
                })
                .decorates(phoneField)
                .immediate();

        validator.createCheck()
                .dependsOn("name", nameField.textProperty())
                .withMethod(c -> {
                    if (((String) c.get("name")).length() < 5) c.error("Ім'я повино містити більше 5 символів");
                })
                .decorates(nameField)
                .immediate();

        validator.createCheck()
                .dependsOn("address", addressField.textProperty())
                .withMethod(c -> {
                    if (((String) c.get("address")).length() < 5) c.error("Адресса повинна бути більше 5 символів");
                })
                .decorates(addressField)
                .immediate();

        validator.createCheck()
                .dependsOn("post", postField.textProperty())
                .withMethod(c -> {
                    if (((String) c.get("post")).length() < 1) c.error("Введіть посаду");
                })
                .decorates(postField)
                .immediate();

        validator.containsErrorsProperty().addListener((observableValue, aBoolean, t1) -> apply.setDisable(t1));
        apply.setDisable(true);
    }

    public void load(Worker worker) {
        nameField.setText(worker.getName());
        addressField.setText(worker.getAddress());
        phoneField.setText(worker.getPhone());
        postField.setText(worker.getPost());
    }

    public Worker getWorker() {
        return validator.containsErrorsProperty().get() ? null : new Worker(0,
                nameField.getText(),
                addressField.getText(),
                phoneField.getText(),
                postField.getText()
        );
    }

}
