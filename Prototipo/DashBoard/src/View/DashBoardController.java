package View;

/**
 * Software Engineering Project: Dashboard Ambientale
 */

import Controller.DataController;
import Controller.EdificioController;
import Controller.GestoreController;
import Controller.SensoreController;
import Model.VO.Gestore;
import Model.VO.Sensor;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sun.xml.internal.bind.v2.model.core.ID;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Integer.parseInt;

public class DashBoardController implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button logoutButton, modificaButton;
    @FXML
    private TextField max, min;
    @FXML
    private TableView Table;
    @FXML
    private TableColumn IDColumn, NumberColumn, ValueColumn, DataColumn;

    private List<Sensor> listasensori;
    private GestoreController controllerGestore;
    private SensoreController controllerSensore;
    private EdificioController controllerEdificio;
    private DataController controllerData;
    private DateFormat format;
    Sensor sensore;
    private boolean close;

    /*
     * Il metodo initialize inizializza i Controller (Gestore, Sensore e Edificio)
     * Inizializza la lista che servirà a riempire la Table View
     */

    @Override
    public void initialize(URL location, ResourceBundle resource){

        controllerGestore = new GestoreController();
        controllerSensore = new SensoreController();
        controllerEdificio = new EdificioController();
        controllerData = new DataController();
        listasensori = new ArrayList<Sensor>();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
        sensore = new Sensor();
        close = true;
        Run();

    }

    /*
     * Il metodo Run, passa al Controller Gestore l'User di chi ha effettuato l'accesso al sistema. I Controller recuperano, l'User, l'Edificio
     * in suo possesso e i relativi sensori con i valori [min e max]. Viene inizializzata la Table View che servirà a mostrare i dati live al cliente
     */

    private void Run(){

        Gestore sessiongest = controllerGestore.getLoggedGestore();
        String idEdificio = controllerGestore.getGestoreEdificio(sessiongest.getUser());
        listasensori = controllerSensore.getSensoriEdificio(idEdificio);
        System.out.println(listasensori);
        ObservableList<Sensor> values = FXCollections.
                observableArrayList();
        IDColumn.setCellValueFactory(new PropertyValueFactory<Sensor, String>("ID"));
        NumberColumn.setCellValueFactory(new PropertyValueFactory<Sensor, Integer>("numSensore"));
        ValueColumn.setCellValueFactory(new PropertyValueFactory<Sensor, Integer>("value"));
        DataColumn.setCellValueFactory(new PropertyValueFactory<Sensor, String>("Time"));

            for (Sensor s : listasensori){
                values.add(s);
            }

        Table.setItems(values);

        /*
         * Il Metodo setRowFactory è un metodo di libreria di FX ed è necessario per modificare le proprietà gradice della TableView sotto determinate
         * condizioni. Il metodo, ogni volta che un nuovo sensore viene aggiornato nella lista, intercetta la modifica (item) e verifica le condizioni
         * in modo da mostrare i "colori" in base al dato che viene inviato dal sensore. (Rosso: Sensore fuori range di molto, Arancione: Fuori range limite,
         * Verde: tutto ok
         */

        Table.setRowFactory(tv -> new TableRow<Sensor>() {
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

        /*
         * Il thread è necessario per permettere la renderizzazione dei valori nella TableView in quanto l'aggiornamento sarebbe troppo veloce e non
         * darebbe tempo a FX di listare i valori. Il thread attraverso un While(true) è in continua richiesta di nuovi valori dal server, Viene
         * effettuato un controllo per ogni sensore presente, se il dato che arriva aggiorna un sensore, solleva il metodo sopra setRowFactory.
         */

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (close) {
                    for (Sensor s : listasensori) {
                        Sensor temp = controllerData.getLastData(s.getID());
                        for (Object items : Table.getItems()) {
                            Sensor temptable = (Sensor) items;
                            if ((temptable.getNumSensore() == temp.getNumSensore())) {
                                Date old = null;
                                Date curr = null;
                                try {
                                    curr = format.parse(temptable.getTime());
                                    old = format.parse(temp.getTime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (old.after(curr)) {
                                    temptable.setTime(temp.getTime());
                                    temptable.setValue(temp.getValue());
                                }
                            }
                            Table.refresh();
                        }
                    }
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
            };
            Thread th = new Thread(task);
            th.setDaemon(false);
            th.start();

        /*
         * Il Thread si occupa della verifica della correttezza di funzionamento dei Sensori. Se passato un minuto da l'ultimo invio il Sensore non
         * risponde, automaticamente viene mostrato un Warning, il valore nella Dashboard è posto a 0. Viene mostrato anche un Alert, che mostra al
         * cliente l'effettivo malfunzionamento. Il controllo Avviene al lato Client per evitare sovraccarichi al Server.
         */



        Task<Void> task2 = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                while (close) {
                    for (Object item : Table.getItems()) {
                        Sensor tempor = (Sensor) item;
                        Date oldval = null;
                        try {

                            if (tempor.getTime() != null)
                                oldval = format.parse(tempor.getTime());
                            else {
                                oldval = new Date();
                                tempor.setTime(format.format(oldval));
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date data = new Date();
                        long diff = data.getTime() - oldval.getTime();
                        long min = (diff / 60000);
                        System.out.println(min);
                        if (min >= 1) {
                            tempor.setValue(0);
                            String time = format.format(new Date());
                            tempor.setTime(time);
                            Platform.runLater(new Runnable() {

                                @Override
                                public void run() {
                                    Alert alert = new Alert(Alert.AlertType.NONE);
                                    alert.setTitle("Possibile malfunzionamento Sensore");
                                    alert.setContentText("Il sensore " + tempor.getNumSensore() + " sembra" +
                                            " non funzionare correttamente");
                                    ButtonType buttonTypeCancel = new ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE);
                                    alert.getButtonTypes().setAll(buttonTypeCancel);
                                    ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("View/icon.png"));

                                    Thread thread = new Thread(() -> {
                                        try {
                                            Thread.sleep(2000);
                                            if (alert.isShowing()) {
                                                Platform.runLater(() -> alert.close());
                                            }
                                        } catch (Exception exp) {
                                            exp.printStackTrace();
                                        }
                                    });

                                    thread.setDaemon(false);
                                    thread.start();
                                    alert.showAndWait();
                                }
                            });

                        }
                        Table.refresh();
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                return null;
            }
        };
        Thread t2 = new Thread(task2);
        t2.setDaemon(false);
        t2.start();

    }

    /*  cliccando su una riga della tabella si assegna ad un oggetto sensore di tipo Sensor il sensore
        presente sulla riga clickata. */

    @FXML
    public void clickItem(MouseEvent event)
    {
        if (event.getClickCount() == 1) //Checking double click
        {
            sensore = (Sensor) Table.getSelectionModel().getSelectedItem();
            System.out.println(sensore.getID());
        }
    }
    
    /*  bottone modifica, dopo aver scritto i nuovi parametri max e/o min premendo sul bottone,
        vengono aggiornati i dati del model e del database del sensore selezionato in precedenza.
        se non é stato selezionato nessun sensore o non si é inserito nessun dato non succede nulla. */

    @FXML
    private void modifica(ActionEvent Event) throws IOException {
        if(sensore!=null) {
            if (parseInt(min.getText()) < parseInt(max.getText()) || min.getText().isEmpty() || max.getText().isEmpty()) {
                if (!min.getText().isEmpty()) {
                    sensore.setMinRange(parseInt(min.getText()));
                    controllerSensore.updateRangeSensoreMin(sensore.getID(), parseInt(min.getText()), sensore.getNumSensore(), sensore.getMaxRange(), sensore.getEdificio());
                }
                if (!max.getText().isEmpty()) {
                    sensore.setMaxRange(parseInt(max.getText()));
                    controllerSensore.updateRangeSensoreMax(sensore.getID(), parseInt(max.getText()), sensore.getNumSensore(), sensore.getMinRange(), sensore.getEdificio());
                }
            }
        }
    }

    // bottone logout, riporta alla loginPage

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

}
