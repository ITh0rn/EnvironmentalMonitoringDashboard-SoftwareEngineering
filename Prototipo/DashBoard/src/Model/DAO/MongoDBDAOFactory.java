package Model.DAO;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import javafx.scene.control.Alert;

import java.net.UnknownHostException;

public class MongoDBDAOFactory extends DAOFactory {

    public static String DRIVER = "10.170.65.67";
    public static int PORT = 27017;
    public static String DATABASE = "progetto";

    /*
        DB Connection, static for all classes. Is not necessary close the connection. MongoDB Drivers do it automatically;
     */

    public static DB createConnection(){

        DB database = null;
        try {
            MongoClient client = new MongoClient(DRIVER, PORT);
            database = client.getDB(DATABASE);
        } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        return database;
    }

    @Override
    public GestoreDAO getGestoreDAO() {
        return new MongoDBGestoreDAOimpl();
    }

    @Override
    public SensorDAO getSensorDAO() {
        return new MongoDBSensorDAOimpl();
    }

    @Override
    public EdificioDAO getEdificioDAO() {
        return new MongoDBEdificioDAOimpl();
    }

    @Override
    public ZonaDAO getZonaDAO() {
        return new MongoDBZonaDAOimpl();
    }

    @Override
    public DataDAO getDataDAO() { return new MongoDBDataDAOimpl(); }

}
