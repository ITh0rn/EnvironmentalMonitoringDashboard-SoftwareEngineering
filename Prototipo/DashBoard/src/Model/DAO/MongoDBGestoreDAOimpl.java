package Model.DAO;

import Model.VO.Gestore;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

// import java.util.Collection;

public class MongoDBGestoreDAOimpl implements GestoreDAO {

    private MongoDBDAOFactory factory = new MongoDBDAOFactory();
    private String COLLECTION = "User";
    private DBCollection edificioCollection = factory.createConnection().getCollection(COLLECTION);
    private DBCollection gestoreCollection = factory.createConnection().getCollection(COLLECTION);

    @Override
    public boolean CorrectLoginData(String username, String password) {

    DBObject query = new BasicDBObject().append("Username", username);
    DBCursor cursor = gestoreCollection.find(query);
    while (cursor.hasNext()) {
        DBObject user = cursor.next();
        if ((user.get("Password")).equals(password)) return true;
    }
    return false;

    }

    @Override
    public Gestore getGestoreInfo(String Username){

        DBCursor gest = gestoreCollection.find(new BasicDBObject()
                .append("Username", Username));
        Gestore gestore = new Gestore();
        while(gest.hasNext()){
            DBObject obj = gest.next();
            gestore.setName((String )obj.get("Name"));
            gestore.setSurname((String) obj.get("Surname"));
            gestore.setUser((String) obj.get("Username"));
            gestore.setPassword((String) obj.get("Password"));
            gestore.setRuolo((String) obj.get("Ruolo"));
        }
        return gestore;

    }

    @Override
    public String getGestoreEdificio(String user){

        edificioCollection = factory.createConnection().getCollection(COLLECTION);
        DBObject edif = edificioCollection.findOne(new BasicDBObject().append("Username", user));
        return (String) edif.get("Edificio");

    }


}
