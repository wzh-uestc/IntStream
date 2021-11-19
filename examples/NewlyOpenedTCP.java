package user_interface;

import core.ExecutionEnvironment;
import driver.stenographer_driver.StenographerDriver;
import local_stream_processor.sink.BlackHoleSink;
import primitives.NetStream;
import primitives.PacketInfo;

public class NewlyOpenedTCP {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = new ExecutionEnvironment(args);
        String local = "stenographer_s14_129";

        env.addLocalEnvironment(local);
        NetStream<PacketInfo> stream = env.get(local).getNetStream(new StenographerDriver());

        NetStream<String> stream2 = stream
                .filter(a-> a.get("TCP.flags") != null && a.get("TCP.flags").equals("S"))
                .setExtractTimestampFunc(a->Long.valueOf(a.get("timestamp")))
                .setExtractKeyFunc(a->a.get("IP.dst"))
                .setTimeWindow(10)
                .transform((elements, out) -> {
                    String sip = "", dip = "";
                    long cnt = 0;
                    for(PacketInfo p: elements){
                        cnt++;
                    }

                    if(cnt > 10) {
                        out.collect(String.valueOf(cnt));
                    }
                });

        BlackHoleSink<String> blackHoleSink = new BlackHoleSink<>();
            stream2.addSink(blackHoleSink);

        env.execute("NewlyOpenedTCP");
    }
}
