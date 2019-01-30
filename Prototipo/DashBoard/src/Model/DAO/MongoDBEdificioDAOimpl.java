package Model.DAO;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;

public class MongoDBEdificioDAOimpl implements EdificioDAO {

    private String COLLECTION = "Edificio";
    private MongoDBDAOFactory factory = new MongoDBDAOFactory();
    private DBCollection edificioCollection = factory.createConnection().getCollection(COLLECTION);


    @Override
    public DBObject getSensoriEdificio(String idEdificio) {

        BasicDBObject query = new BasicDBObject().append("_id", new ObjectId(idEdificio));
        DBObject edificio = edificioCollection.findOne(query);
        return (DBObject) edificio.get("Sensori");

    }
}
