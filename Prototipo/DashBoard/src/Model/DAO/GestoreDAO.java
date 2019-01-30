package Model.DAO;

import Model.VO.Gestore;

public interface GestoreDAO {

    public boolean CorrectLoginData(String username, String password);
    public String getGestoreEdificio(String username);
    public Gestore getGestoreInfo(String Username);
}
