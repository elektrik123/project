package ua.opu.shveda.databaseprocessor;

import com.dlsc.formsfx.model.structure.*;
import com.dlsc.formsfx.view.controls.SimplePasswordControl;
import com.dlsc.formsfx.view.controls.SimpleTextControl;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ua.opu.shveda.databaseprocessor.controller.MainView;
import ua.opu.shveda.databaseprocessor.model.Service;

import java.io.IOException;

public class DBPApp extends Application {
    @Override
    public void start(Stage mainStage) throws IOException {
        Stage loginStage = new Stage();

        SimpleStringProperty login = new SimpleStringProperty("");
        SimpleStringProperty password = new SimpleStringProperty("");
        Button submit = new Button("Увійти");
        HBox submitBox = new HBox(submit);
        submitBox.setPadding(new Insets(20, 0, 0, 0));
        submitBox.setAlignment(Pos.CENTER);
        Form loginForm = Form.of(
                Group.of(
                        Field.ofStringType(login).label("Логін: ")
                                .required("Ведіть логін")
//                                .validate(CustomValidator.forPredicate(s -> s.length() > 4, "Логін повинен бути довше 4 сиволів"))
                                .render(new SimpleTextControl()),
                        Field.ofPasswordType(password).label("Пароль: ")
                                .required("Ведіть пароль")
//                                .validate(CustomValidator.forPredicate(s -> s.length() > 8, "Пароль повинен бути довше 8 сиволів"))
                                .render(new SimplePasswordControl()),
                        NodeElement.of(submitBox)
                )
        );
        Scene loginScene = new Scene(new FormRenderer(loginForm), 400, 200);

        submit.setOnAction(e -> {
            loginForm.persist();
            if (Service.getInstance().authenticateUser(login.get(), password.get())) {
                Service.getInstance().authentication = Service.getInstance().getUser(login.get());
                loginStage.close();
                login.set("");
                password.set("");
                loginForm.reset();
                try {
                    mainStage.setScene(new Scene(new MainView(mainStage, loginStage), 950, 650));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                mainStage.show();
            }
        });

        loginStage.setTitle("Вхід");
        loginStage.setScene(loginScene);
        loginStage.show();

        mainStage.setTitle("DB processor!");
    }

    public static void main(String[] args) throws ClassNotFoundException {

        Class.forName("org.sqlite.JDBC");

        launch();
    }
}