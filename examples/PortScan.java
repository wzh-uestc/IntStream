package user_interface;

import core.ExecutionEnvironment;
import driver.stenographer_driver.StenographerDriver;
import local_stream_processor.sink.BlackHoleSink;
import primitives.NetStream;
import primitives.PacketInfo;
import java.util.HashSet;

public class PortScan {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = new ExecutionEnvironment(args);
        String local = "stenographer_s14_129";

        env.addLocalEnvironment(local);
        NetStream<PacketInfo> stream = env.get(local).getNetStream(new StenographerDriver());

        NetStream<String> stream2 = stream
                .filter(a-> a.get("IP.protocal").equals("6"))
                .setExtractTimestampFunc(a->Long.valueOf(a.get("timestamp")))
                .setExtractKeyFunc(a->a.get("IP.src"))
                .setTimeWindow(10)
                .transform((elements, out) -> {
                    HashSet<String> ports = new HashSet<>();
                    String sip = "";
                    for(PacketInfo p: elements){
                        sip = p.get("IP.src");
                        ports.add(p.get("TCP.dstPort"));
                    }

                    if(ports.size() > 100){
                        out.collect(sip);
                    }
                });

        BlackHoleSink<String> blackHoleSink = new BlackHoleSink<>();
        stream2.addSink(blackHoleSink);

        env.execute("PortScan");
    }
}
