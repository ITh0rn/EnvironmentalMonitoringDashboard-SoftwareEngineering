package Model.VO;

import java.util.List;

public class Edificio {

    private String Nome;
    private String Zona;
    private int numSensori;
    private String Owner;
    private List<Sensor> list;
    private int levelerror;

    public Edificio(){

    }

    public Edificio(String Nome, String zona, int num, String ow, List<Sensor> list) {
        this.Nome = Nome;
        this.list = list;
        this.Zona = zona;
        this.numSensori = num;
        this.Owner = ow;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String address) {
        Nome = address;
    }

    public String getZona() {
        return Zona;
    }

    public void setZona(String zona) {
        Zona = zona;
    }

    public int getNumSensori() {
        return numSensori;
    }

    public void setNumSensori(int numSensori) {
        this.numSensori = numSensori;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public List<Sensor> getList() {
        return list;
    }

    public void setList(List<Sensor> list) {
        this.list = list;
    }

    public int getLevelerror() {
        return levelerror;
    }

    public void setLevelerror(int levelerror) {
        this.levelerror = levelerror;
    }
}
