package Model;

import com.google.cloud.storage.Blob;

public class MyBlob implements Comparable<MyBlob>{
    private Blob blob;
    private long time;

    public long getTime() {
        return blob.getCreateTime();
    }

    public MyBlob(Blob blob){
        this.blob=blob;
    }

    public String toString() {
        return blob.toString();
    }

    @Override
    public int compareTo(MyBlob b) {
        if (this.blob.getCreateTime()>b.blob.getCreateTime()){
            return -1;
        }
        else if(this.blob.getCreateTime()<b.blob.getCreateTime()){
            return 1;
        }
        else{
            return 0;
        }
    }
}
