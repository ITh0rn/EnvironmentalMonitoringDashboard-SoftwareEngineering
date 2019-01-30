package Controller;

import Model.DAO.DAOFactory;
import Model.DAO.ZonaDAO;
import Model.VO.Edificio;
import Model.VO.Zona;
import java.util.List;

public class ZonaController {

    private static final int MONGODB = 0;
    private DAOFactory mongoDBFactory;
    private ZonaDAO zonaDAO;

    public ZonaController(){
    mongoDBFactory = DAOFactory.getDAOFactory(MONGODB);
    zonaDAO = mongoDBFactory.getZonaDAO();
    }

    public List<Edificio> getEdificiCIttà(String User){
        return zonaDAO.getEdifici(User);
    }
}
