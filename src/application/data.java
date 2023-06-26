package src.application;
import java.io.Serializable;

public class data implements Serializable {
    public String data;
    public String type;
    public int dataLength;

    // Constructor
    public data(String data, String type, int len) {
        this.data = data;
        this.type = type;
        this.dataLength = len;
    }
}
