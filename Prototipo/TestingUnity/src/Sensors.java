

public class Sensors implements Comparable<Sensors> {

    private String ID;
    private int priotità;
    private int minRange;
    private int maxRange;
    private int numSensore;
    private int value;
    private String Time;

    public Sensors(){

    }

    public Sensors(String id, int pror, int min, int max, int value, String tm){
        this.ID = id;
        this.priotità = pror;
        this.minRange = min;
        this.maxRange = max;
        this.value = value;
        this.Time = tm;
    }

    public String getID(){
        return this.ID;
    }

    public void setID(String id){
        this.ID = id;
    }

    public int getPriotità(){
        return this.priotità;
    }

    public void setPriotità(int prior){
        this.priotità = prior;
    }

    public int getMinRange(){
        return this.minRange;
    }

    public void setMinRange(int min){
        this.minRange = min;
    }

    public int getMaxRange(){
        return this.maxRange;
    }

    public void setMaxRange(int max){
        this.maxRange = max;
    }

    public int getNumSensore(){
        return this.numSensore;
    }

    public void setNumSensore(int num){
        this.numSensore = num;
    }

    public int getValue(){
        return this.value;
    }

    public void setValue(int va){
        this.value = va;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    @Override
    public int compareTo(Sensors other){
        if ((this.priotità - other.priotità) == 0) return 0;
        else
            return this.priotità - other.priotità;
    }


}
