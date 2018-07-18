package Model;

public class OLDLineChartObject implements Comparable<OLDLineChartObject> {
    private static int count = 0;
    private int location;
    private int data;

    public OLDLineChartObject(int data) {
        this.location = count;
        this.data = data;
        ++count;
    }

    public static int getCount() {
        return count;
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

    public void setData(int data) {
        this.data = data;
    }

    @Override
    public int compareTo(OLDLineChartObject l) {
        if (this.location > l.location)
            return 1;
        else if (this.location < l.location)
            return -1;
        else
            return 0;
    }
}
