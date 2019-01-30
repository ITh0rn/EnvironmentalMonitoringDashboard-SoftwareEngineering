package Model.DAO;

import com.mongodb.DBObject;

public interface EdificioDAO {

    public DBObject getSensoriEdificio(String idEdificio);

}
