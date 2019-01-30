package Controller;

import Model.DAO.DAOFactory;
import Model.DAO.EdificioDAO;
import com.mongodb.DBObject;

public class EdificioController {

    private static final int MONGODB = 0;
    private DAOFactory mongoDBFactory;
    private EdificioDAO edificioDAO;

    public EdificioController(){
        mongoDBFactory = DAOFactory.getDAOFactory(MONGODB);
        edificioDAO = mongoDBFactory.getEdificioDAO();
    }

    public DBObject getSensoriEdificio(String idEdificio){
        DBObject obj = edificioDAO.getSensoriEdificio(idEdificio);
        return obj;
    }


}
