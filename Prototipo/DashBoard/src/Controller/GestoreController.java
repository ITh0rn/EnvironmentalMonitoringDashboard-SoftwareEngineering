package Controller;

import Model.DAO.DAOFactory;
import Model.DAO.GestoreDAO;
import Model.VO.Gestore;
import com.mongodb.DBObject;

public class GestoreController {

    private static final int MONGODB = 0;
    private DAOFactory mongoDBFactory;
    private GestoreDAO gestoreDAO;
    private static Gestore loggedGestore;

    public GestoreController(){
        mongoDBFactory = DAOFactory.getDAOFactory(MONGODB);
        gestoreDAO = mongoDBFactory.getGestoreDAO();
    }

    public String getGestoreEdificio (String User){

        String IDedificio = gestoreDAO.getGestoreEdificio(User);
        return IDedificio;

    }

    public Gestore getGestore(String Username){
        return gestoreDAO.getGestoreInfo(Username);
    }


    public boolean getin(String username, String password ) {
        return gestoreDAO.CorrectLoginData(username, password);
    }

    public void setGestoreLogged(Gestore log){
        loggedGestore = log;
    }

    public Gestore getLoggedGestore(){
        return loggedGestore;
    }
    }
