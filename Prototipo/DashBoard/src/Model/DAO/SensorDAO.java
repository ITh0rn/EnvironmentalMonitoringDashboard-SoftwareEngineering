package Model.DAO;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import java.util.List;

public interface SensorDAO {

    public DBCursor getSensoriEdificio(String id);
    public void updateRangeSensoreMin(String id, int min, int numb, int max, String ed);
    public void updateRangeSensoreMax(String id, int max, int numb, int min, String ed);
}
