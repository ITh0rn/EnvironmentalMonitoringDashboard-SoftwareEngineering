package View;

/**
 * Software Engineering Project: Dashboard Ambientale
 */

import Controller.*;
import Model.VO.Edificio;
import Model.VO.Gestore;
import Model.VO.Sensor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DashboardcittaController implements Initializable {

    @FXML
    private Button logoutButton, logoutButton1;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView Table, Table1;
    @FXML
    private TableColumn NameColumn, ZoneColumn, numColumn, GestoreColumn, NumberColumn,
        ValueColumn, TimeColumn;

    private List<Edificio> listaedifici;
    private GestoreController controllerGestore;
    private SensoreController controllerSensore;
    private EdificioController controllerEdificio;
    private ZonaController controllerZona;
    private DataController controllerData;
    private DateFormat format;
    ObservableList<Sensor> valuesSens;
    private boolean close;

    @Override
    public void initialize(URL location, ResourceBundle resource){

        controllerGestore = new GestoreController();
        controllerSensore = new SensoreController();
        controllerEdificio = new EdificioController();
        controllerZona = new ZonaController();
        controllerData = new DataController();
        listaedifici = new ArrayList<Edificio>();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
        close = true;
        Run();

    }

    private void Run(){

        Gestore logged = controllerGestore.getLoggedGestore();
        listaedifici = controllerZona.getEdificiCIttà(logged.getUser());
        ObservableList<Edificio> values = FXCollections.
                observableArrayList();
        valuesSens = FXCollections.
                observableArrayList();
        NameColumn.setCellValueFactory(new PropertyValueFactory<Edificio, String>("Nome"));
        ZoneColumn.setCellValueFactory(new PropertyValueFactory<Edificio, String>("Zona"));
        GestoreColumn.setCellValueFactory(new PropertyValueFactory<Edificio, String>("Owner"));
        numColumn.setCellValueFactory(new PropertyValueFactory<Edificio, Integer>("numSensori"));
        NumberColumn.setCellValueFactory(new PropertyValueFactory<Sensor, Integer>("numSensore"));
        ValueColumn.setCellValueFactory(new PropertyValueFactory<Sensor, Integer>("value"));
        TimeColumn.setCellValueFactory(new PropertyValueFactory<Sensor, Integer>("Time"));

        for (Edificio e : listaedifici){
            int count = 0;
            List<Sensor> listasensori= controllerSensore.getSensoriEdificio(e.getNome());
            e.setList(listasensori);
            for (Sensor s : listasensori){
                count++;
                s.setTime(format.format(new Date()));
                //valuesSens.add(s);
            }
            System.out.println(count);
            e.setNumSensori(count);
            values.add(e);
        }

        Table.setItems(values);


        Table.setRowFactory(tv -> new TableRow<Edificio>() {
            @Override
            public void updateItem(Edificio item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("-fx-background-color: #ffffff;");
                } else {
                    if ( item.getLevelerror() == 1 ){
                        setStyle("-fx-background-color: #007000;");
                    }
                    if(item.getLevelerror() == 2 ) setStyle("-fx-background-color: #de8101;");
                    if(item.getLevelerror() == 3 ) setStyle("-fx-background-color: #9e0911;");
                }
            }
        });

        Table1.setRowFactory(tv -> new TableRow<Sensor>() {
            @Override
            public void updateItem(Sensor item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("-fx-background-color: #ffffff;");
                } else {
                    int value = item.getValue(), max = item.getMaxRange(), min = item.getMinRange();
                    if (value > max + 3 || value < min - 3) setStyle("-fx-background-color: #9e0911;");
                    if ((value > max  && value <= max + 3 ) || (value < min && value >= min - 3))
                        setStyle("-fx-background-color: #de8101;");
                    if (value >= min && value <= max) setStyle("-fx-background-color: #007000;");
                }
            }
        });


        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (close) {
                    for (Object e : Table.getItems()) {
                        for (Sensor s : ((Edificio) e).getList()) {
                            Sensor temp = controllerData.getLastData(s.getID());
                            for (Sensor s2 : ((Edificio) e).getList()) {
                                if (temp.getNumSensore() == s2.getNumSensore()) {
                                    Date newtime = null;
                                    Date oldtime = null;
                                    try {
                                        newtime = format.parse(s2.getTime());
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                    try {
                                        oldtime = format.parse(temp.getTime());
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                    if (oldtime.after(newtime)) {
                                        s2.setTime(temp.getTime());
                                        s2.setValue(temp.getValue());
                                        for (Object obj : Table1.getItems()) {
                                            Sensor temp2 = (Sensor) obj;
                                            if (temp2.getID().equals(s2.getID()))
                                                temp2.setValue(temp.getValue());
                                        }
                                        Table1.refresh();
                                    } else {
                                        Date newTime = new Date();
                                        long diff = (newTime.getTime() - newtime.getTime()) / 60000;
                                        if (diff >= 1) {
                                            s2.setTime(format.format(newTime));
                                            s2.setValue(0);
                                            for (Object obj : Table1.getItems()) {
                                                Sensor temp2 = (Sensor) obj;
                                                if (temp2.getID().equals(s2.getID()))
                                                    temp2.setValue(0);
                                            }
                                            Table1.refresh();
                                        }
                                    }
                                }
                            }
                            Table.refresh();
                        }
                    }
                    try {
                        Thread.sleep(280);
                    } catch (InterruptedException m) {
                        m.printStackTrace();
                    }
                }
                return null;
            }
        };
        Thread t1 = new Thread(task);
        t1.setDaemon(true);
        t1.start();



        Task<Void> controllerror = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                while (close) {
                    for (Object itemtab : Table.getItems()) {
                        Edificio e = (Edificio) itemtab;
                        int count = e.getNumSensori();
                        float err = 0;
                        for (Sensor s : e.getList()) {
                            int value = s.getValue(), maxra = s.getMaxRange(), minra = s.getMinRange();
                            if (value > (maxra + 3) || value < (minra - 3)) {
                                err += 1;
                            }
                        }
                        float res = (err / count);
                        System.out.println(res);
                        if (res >= 0.80) {
                            e.setLevelerror(3);
                        }
                        if ((res > 0.60) && (res < 0.80)) {
                            e.setLevelerror(2);
                        }
                        if (res <= 0.60) {
                            e.setLevelerror(1);
                        }
                        Table.refresh();
                    }
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
        Thread t2 = new Thread(controllerror);
        t2.setDaemon(true);
        t2.start();

    }

    @FXML
    private void logout(ActionEvent Event) throws IOException {

        try {
            Stage windows = (Stage) rootPane.getScene().getWindow();
            close = false;
            windows.close();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("loginPage.fxml"));
            Parent root = (Parent) loader.load();
            Stage login = new Stage();
            login.setScene(new Scene(root));
            login.setTitle("Biblioteca Digitale UNIVAQ");
            login.show();
            login.setResizable(false);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void Visualizza(ActionEvent Event) throws IOException {

            valuesSens.removeAll();
            Table1.getItems().clear();
            Edificio selected = (Edificio) Table.getSelectionModel().getSelectedItem();
            List<Sensor> mostavalori = new ArrayList<Sensor>();
            List<Object> obj = Table.getItems();
            for (Object tableed : Table.getItems()){
                Edificio temp = (Edificio) tableed;
                if (selected.getNome().equals(temp.getNome())){
                    mostavalori = temp.getList();
                }
            }
            for (Sensor s : mostavalori)
            valuesSens.add(s);
            Table1.setItems(valuesSens);

        }
    }

