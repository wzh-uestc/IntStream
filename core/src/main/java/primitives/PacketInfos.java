package primitives;



import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class PacketInfos {
    public HashMap<String, String> field_mp;
    private static Function<PacketInfos, Long> key_gen_fun;

    public PacketInfos(){
        this.field_mp = new HashMap<>();
    }

    public PacketInfos(ObjectNode jsonNodes){
        this.field_mp = new HashMap<>();

        Iterator<String> iterator = jsonNodes.get("value").fieldNames();

        while(iterator.hasNext()){
            String f = iterator.next();
            this.field_mp.put(f, jsonNodes.get("value").get(f).asText());
        }
    }

    public String get(String field){
        return this.field_mp.get(field);
    }

    public static void set_key_method(Function<PacketInfos, Long> function){
        key_gen_fun = function;
    }

    public static Long ipToLong(String ip){
        String[] nums = ip.split("\\.");
        int[] weights = {16777216, 65536, 256, 1};
        long res = 0;

        for(int i = 0; i < nums.length; i++){
            long num = Long.parseLong(nums[i]);
            res += num * weights[i];
        }

        return res;
    }

    public static Long get_key(PacketInfos p){
        if(key_gen_fun == null){
            //key_gen_fun is not set
            System.out.println("key_gen_fun not set. Default key: sip");
            return ipToLong(p.get("IP.src"));
        }
        return key_gen_fun.apply(p);
    }

    @Override
    public String toString() {
        String res = "";
        for(Map.Entry<String, String> e: this.field_mp.entrySet()){
            res += e.getKey() + ": " + e.getValue() + "\t";
        }
        return res;
    }
}
