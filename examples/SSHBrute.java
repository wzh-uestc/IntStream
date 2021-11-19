package user_interface;

import core.ExecutionEnvironment;
import driver.stenographer_driver.StenographerDriver;
import local_stream_processor.sink.BlackHoleSink;
import primitives.NetStream;
import primitives.PacketInfo;

import java.util.HashMap;

public class SSHBrute {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = new ExecutionEnvironment(args);
        String local = "stenographer_s14_129";

        env.addLocalEnvironment(local);
        NetStream<PacketInfo> stream = env.get(local).getNetStream(new StenographerDriver());

        NetStream<String> stream2 = stream
                .setExtractTimestampFunc(a -> Long.valueOf(a.get("timestamp")))
                .setExtractKeyFunc(a -> a.get("IP.dst"))
                .setTimeWindow(10)
                .transform((elements, out) -> {
                    HashMap<String, Integer> map = new HashMap<>();

                    for(PacketInfo p: elements){
                        String key = p.get("IP.dst") + "_" + p.get("IP.src");
                        int cnt = map.getOrDefault(key, 0);
                        cnt += Integer.valueOf(p.get("length"));
                        map.put(key, cnt);

                        if(cnt > 50){
                            out.collect(p.get("IP.dst"));
                            break;
                        }
                    }
                });

        BlackHoleSink<String> blackHoleSink = new BlackHoleSink<>();
        stream2.addSink(blackHoleSink);
    }
}
