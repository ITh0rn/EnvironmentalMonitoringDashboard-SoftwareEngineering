package View;


import Controller.GestoreController;
import Model.VO.Gestore;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.Node;

public class loginPageController {


    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label getin;
    @FXML

    private GestoreController gestore = new GestoreController();

    @FXML
    public void login(ActionEvent actionEvent) {
        if (gestore.getin(username.getText(), password.getText())) {
            try {
                Gestore single = gestore.getGestore(username.getText());
                gestore.setGestoreLogged(single);
                if (single.getRuolo().equals("Gestore edificio")) {
                    ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
                    Parent root = (Parent) fxmlLoader.load();
                    Stage stage1 = new Stage();
                    stage1.setTitle("Dashboard Edificio - Visualizza Dati");
                    stage1.getIcons().add(new Image("View/icon.png"));
                    stage1.setScene(new Scene(root));
                    stage1.show();
                }
                else {
                    ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Dashboardcitta.fxml"));
                    Parent root = (Parent) fxmlLoader.load();
                    Stage stage1 = new Stage();
                    stage1.setTitle("Dashboard Zona - Visualizza Dati");
                    stage1.getIcons().add(new Image("View/icon.png"));
                    stage1.setScene(new Scene(root));
                    stage1.show();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        else {
            getin.setText("errore di accesso, usename o password errato/i");
        }

    }
}