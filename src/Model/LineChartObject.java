package Model;

public class LineChartObject {
    private static int count = 0;
    private int location;
    private int data;

    public LineChartObject(int data){
        this.location = count;
        this.data = data;
        ++count;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getData() {
        return data;
    }

    public static int getCount() {
        return count;
    }

    public void setData(int data) {
        this.data = data;
    }

}