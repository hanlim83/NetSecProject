package Model;

import java.util.ArrayList;

public class ObjectConversion {
    private ArrayList<String> bucketsname;
    private String allBucketsName;



    public ObjectConversion(){

    }


    public ObjectConversion(ArrayList<String> bucketname){
        this.bucketsname=bucketname;
        allBucketsName = bucketsname.toString();
        System.out.println("BUCKETS NAME TO STRING  : " + bucketsname.toString());
        for(int i=0;i<bucketsname.size();i++) {
            System.out.println("OBJECT CONVERSION CLASS BUCKET NAMES: " + bucketsname.get(i));
        }
    }

    public ArrayList<String> getBucketsname() {
        return bucketsname;
    }

    public void setBucketsname(ArrayList<String> bucketsname) {
        this.bucketsname = bucketsname;
    }
    public String getAllBucketsName() {
        return allBucketsName;
    }

    public void setAllBucketsName(String allBucketsName) {
        this.allBucketsName = allBucketsName;
    }
}

