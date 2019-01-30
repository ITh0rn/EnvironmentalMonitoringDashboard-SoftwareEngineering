package Model.DAO;

import Model.VO.Sensor;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

public class MongoDBDataDAOimpl implements DataDAO {

    private String COLLECTION = "Data";
    private MongoDBDAOFactory factory = new MongoDBDAOFactory();
    private DBCollection dataCollection = factory.createConnection().getCollection(COLLECTION);


    @Override
    public Sensor getData(String idSensore) {

        DBCursor livesen = dataCollection.find(new BasicDBObject().append("IDSensore", idSensore))
                .sort(new BasicDBObject("_id", -1)).limit(1);
            DBObject sen = livesen.next();
            Sensor s = new Sensor();
            s.setNumSensore((int) sen.get("Number"));
            s.setValue((int) sen.get("Temp"));
            s.setTime(sen.get("TimeStamp").toString());
        return s;

    }

}
