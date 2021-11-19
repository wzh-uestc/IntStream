package user_interface;

import core.ExecutionEnvironment;
import driver.stenographer_driver.StenographerDriver;
import local_stream_processor.sink.BlackHoleSink;
import primitives.NetStream;
import primitives.PacketInfo;

import java.util.HashSet;

public class SYNFlood {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = new ExecutionEnvironment(args);
        String local = "stenographer_s14_129";

        env.addLocalEnvironment(local);
        NetStream<PacketInfo> stream = env.get(local).getNetStream(new StenographerDriver());

        NetStream<String> stream2 = stream
                .filter(a -> a.get("TCP.flags").contains("S"))
                .setExtractTimestampFunc(a -> Long.valueOf(a.get("timestamp")))
                .setExtractKeyFunc(a -> a.get("IP.dst"))
                .setTimeWindow(10)
                .transform((elements, out) -> {
                    int synCnt = 0, synackCnt = 0;
                    String dip = "";
                    for (PacketInfo p : elements) {
                        dip = p.get("IP.dst");
                        if(p.get("TCP.flags").equals("S")){
                            synCnt++;
                        }
                        else if (p.get("TCP.flags").equals("AS")){
                            synackCnt++;
                        }
                    }

                    if (synCnt - synackCnt > 100) {
                        out.collect(dip);
                    }
                });

        BlackHoleSink<String> blackHoleSink = new BlackHoleSink<>();
        stream2.addSink(blackHoleSink);
    }
}
