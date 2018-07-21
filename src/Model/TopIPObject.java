package Model;

public class TopIPObject implements Comparable<TopIPObject> {
    private int value;
    private String key;

    public TopIPObject(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int compareTo(TopIPObject o) {
        if (this.value > o.value)
            return -1;
        else if (this.value < o.value)
            return 1;
        else
            return 0;
    }
}
