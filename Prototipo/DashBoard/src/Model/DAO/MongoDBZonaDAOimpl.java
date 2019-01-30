package Model.DAO;

import Model.VO.Edificio;
import com.mongodb.*;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.ArrayList;
import java.util.List;

public class MongoDBZonaDAOimpl implements ZonaDAO {

    private String COLLECTION = "User";
    private String COLLECTION2 = "Edificio";
    private MongoDBDAOFactory factory = new MongoDBDAOFactory();
    private DBCollection userCollection = factory.createConnection().getCollection(COLLECTION);
    private DBCollection edificioCollection = factory.createConnection().getCollection(COLLECTION2);

    @Override
    public List<Edificio> getEdifici(String User) {
        List<Edificio> lista = new ArrayList<Edificio>();
        DBCursor gest = userCollection.find(new BasicDBObject()
        .append("Username", User));
        DBObject obj = gest.next();
        BasicDBList listaedificio = (BasicDBList) obj.get("Edifici");
        for (Object o : listaedificio){
            System.out.println(o);
            Edificio e = new Edificio();
            DBCursor cur = edificioCollection.find(new BasicDBObject()
            .append("NomeEdificio", o));
            while(cur.hasNext()){
                DBObject obj2 = cur.next();
                e.setNome((String) obj2.get("NomeEdificio"));
                e.setZona((String) obj2.get("Zona"));
                e.setOwner((String) obj2.get("Owner"));
                e.setLevelerror(0);
                System.out.println(e.getNome());
            }
        lista.add(e);
        }

        return lista;
    }
}
