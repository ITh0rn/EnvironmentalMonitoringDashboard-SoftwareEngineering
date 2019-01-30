package Model.VO;

import java.util.ArrayList;
import java.util.List;

public class Gestore {

    private String Name;
    private String Surname;
    private String User;
    private String Password;
    private int Age;
    private List<?> sensori;
    private String ruolo;

    /*public Gestore(String edificio){

        if (this.ruolo.equals("Edificio"))
        sensori = new ArrayList<Edificio>();
        else sensori = new ArrayList<Zona>();

    }*/

    public Gestore(){

    }

    public Gestore(String Nm, String Sn, String Us, String Ps, int Ag, List<Sensor> list){
        this.Name = Nm;
        this.Surname = Sn;
        this.User = Us;
        this.Password = Ps;
        this.Age = Ag;
        this.sensori = list;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }

    public List<?> getSensori() {
        return sensori;
    }

    public void setSensori(List<?> sensori) {
        this.sensori = sensori;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }
}
