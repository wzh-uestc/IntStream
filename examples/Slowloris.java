package user_interface;

import core.ExecutionEnvironment;
import driver.stenographer_driver.StenographerDriver;
import local_stream_processor.sink.BlackHoleSink;
import primitives.NetStream;
import primitives.PacketInfo;

import java.util.HashSet;

public class Slowloris {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = new ExecutionEnvironment(args);
        String local = "stenographer_s14_129";

        env.addLocalEnvironment(local);
        NetStream<PacketInfo> stream = env.get(local).getNetStream(new StenographerDriver());

        NetStream<String> stream2 = stream
                .filter(a -> a.get("IP.protocal").equals("6"))
                .setExtractTimestampFunc(a -> Long.valueOf(a.get("timestamp")))
                .setExtractKeyFunc(a -> a.get("IP.dst"))
                .setTimeWindow(10)
                .transform((elements, out) -> {
                    HashSet<String> conns = new HashSet<>();
                    int bytes = 0;
                    String dip = "";

                    for(PacketInfo p: elements){
                        dip = p.get("IP.dst");
                        conns.add(p.get("IP.src") + "_" + p.get("TCP.sport") + "_" + p.get("IP.dst"));
                        bytes += Integer.valueOf(p.get("length"));
                    }

                    if(bytes / conns.size() < 50){
                        out.collect(dip);
                    }
                });

        BlackHoleSink<String> blackHoleSink = new BlackHoleSink<>();
        stream2.addSink(blackHoleSink);
    }
}
