package Model.DAO;

public abstract class DAOFactory {

    public static final int MONGODB = 0;
    public static final int MYSQL = 1;

    public abstract GestoreDAO getGestoreDAO();

    public abstract SensorDAO getSensorDAO();

    public abstract EdificioDAO getEdificioDAO();

    public abstract ZonaDAO getZonaDAO();

    public abstract DataDAO getDataDAO();

    public static DAOFactory getDAOFactory(int database) {
        switch (database) {
            case MONGODB:
                return new MongoDBDAOFactory();
            case MYSQL:
                return null;
            default:
                return null;
        }
    }

}