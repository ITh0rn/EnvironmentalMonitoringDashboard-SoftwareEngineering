package Model.DAO;

import Model.VO.Edificio;

import java.util.List;

public interface ZonaDAO {

    public List<Edificio> getEdifici(String User);
}
