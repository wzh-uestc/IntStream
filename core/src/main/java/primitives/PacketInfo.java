package primitives;

import java.util.HashMap;

public class PacketInfo {
    private HashMap<String, String> fields = new HashMap<>();

    public String get(String field){
        return this.fields.get(field);
    }

    public void set(String field, String value){
        this.fields.put(field, value);
    }

    @Override
    public String toString() {
        String res = String.valueOf(this.fields);
        return res;
    }
}
