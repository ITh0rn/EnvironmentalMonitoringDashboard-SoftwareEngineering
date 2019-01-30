
import com.mongodb.*;

import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestClassSimulator implements Runnable{

    private MongoClient client;
    private DB database;
    private DBCollection collection1;
    private DBCollection collection2;
    private List<Sensors> normalpriority;
    private  List<Sensors> highPrior;
    private DateFormat format;
    Random r;

    public TestClassSimulator(){

        try {
            client = new MongoClient("10.170.65.67", 27017);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        database = client.getDB("progetto");
        collection1 = database.getCollection( "Sensor" );
        collection2 = database.getCollection( "Data" );
        normalpriority = new ArrayList<Sensors>();
        highPrior = new ArrayList<Sensors>();
        generateNormalPriority();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        r = new Random();
        Thread t = new Thread(this);
        t.start();
        ExecutorService executor = Executors.newFixedThreadPool(8);
        for(int i = 0; i<6; i++) {
            executor.execute(new HighPriority());
        }
    }

    /*
        Main Thread. This Thread take a list of 150k items and select with a random function the index's item;
        for each file generate a new JSON file;
     */

    @Override
    public void run() {

        Thread t = new Thread(new HighPriority());
        t.start();
        System.out.println("Thread normal priority");
        while(true){
            synchronized (normalpriority) {
                if (!normalpriority.isEmpty()) {
                    int index;
                    if (normalpriority.size() != 1) {
                        index = r.nextInt((normalpriority.size()));
                    } else {
                        index = 0;
                    }
                    Sensors test = normalpriority.get(index);
                    int faketemp = fakeTemperature(test.getMinRange(), test.getMaxRange());
                        sendPost(test, faketemp);
                    System.out.println("Normal Priority: " +test.getNumSensore());
                    if (faketemp < test.getMinRange() || faketemp > test.getMaxRange()) {
                        test.setPriotità(3);
                        normalpriority.remove(test);
                        highPrior.add(test);
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

     /*
        Second Thread. it has an High priority and manage a different list of items, at the start is empty.
     */

    class HighPriority implements Runnable{

        public HighPriority(){

        }
        @Override
        public void run()  {
            System.out.println("Thread high Priority");

            while (true) {
                synchronized (highPrior) {
                    if (!highPrior.isEmpty()) {
                        int index;
                        if (highPrior.size() != 1) {
                            index = r.nextInt((highPrior.size()));
                        } else {
                            index = 0;
                        }
                        Sensors test = highPrior.get(index);
                        System.out.println("High Priority: " +test.getNumSensore());
                        int faketemp = fakeTemperature(test.getMinRange(), test.getMaxRange());
                        test.setTime(new Timestamp(new Date().getTime()).toString() );
                        if (test.getPriotità() > 0) {
                            sendPost(test, faketemp);
                            test.setPriotità(test.getPriotità() - 1);

                        } else {
                            highPrior.remove(test);
                            normalpriority.add(test);
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }



    }

    /*
       Generate fake temperature in a range
     */

    public int fakeTemperature(int min, int max){
        int temp = 0;
        temp = r.nextInt((max + 4) - (min -3)) + min;
        return temp;
    }

    /*
        Create a number of items and add them in a List (normal)
     */

    public void generateNormalPriority(){
        DBCursor cursor = collection1.find();
        while (cursor.hasNext()){
            DBObject obj = cursor.next();
            Sensors s = new Sensors();
            s.setID(obj.get( "_id" ).toString());
            s.setMinRange( (int) obj.get( "MinRange" ) );
            s.setMaxRange( (int) obj.get( "MaxRange" ) );
            s.setNumSensore( (int) obj.get( "Number" ) );
            normalpriority.add( s );
        }
    }

    /*
        Send a post to the main server in a JSON file format with a UTF-8 standard;
        NB: Da completare
     */

    private void sendPost(Sensors s, int faketemp)  {

        Date data = new Date();
        String time = format.format(data);

        DBObject sens = new BasicDBObject().append("Temp", faketemp).
                append("IDSensore", s.getID() ).
                append("Number", s.getNumSensore()).
                append("TimeStamp", time);
        collection2.insert(sens);
    }

    /*
        Main class
     */

    public static void main(String [] args){

        ExecutorService executor = Executors.newFixedThreadPool(10);
        for(int i = 0; i<20; i++) {
            executor.execute(new TestClassSimulator());
        }
    }

}
