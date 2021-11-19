package user_interface;

import core.ExecutionEnvironment;
import driver.stenographer_driver.StenographerDriver;
import local_stream_processor.Collector;
import local_stream_processor.functions.FlatMapFunction;
import local_stream_processor.sink.LSPKafkaProducer;
import primitives.NetStream;
import primitives.PacketInfo;

import java.util.HashMap;
import java.util.Map;

public class Retransmission {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = new ExecutionEnvironment(args);

        String local = "stenographer_s14_129";
        env.addLocalEnvironment(local);
        NetStream<PacketInfo> stream = env.get(local).getNetStream(new StenographerDriver(1608270614, -1));

        NetStream<String> stream2 = stream
                .filter(a->a.get("IP.protocol").equals("6"))
                .setExtractTimestampFunc(a->Long.valueOf(a.get("timestamp")))
                .setExtractKeyFunc((a->a.get("IP.src") + "_" + a.get("TCP.srcPort") + "_"
                 + a.get("IP.dst") + "_" + a.get("TCP.dstPort")))
                .setTimeWindow(20)
                .transform((elements, out) -> {
                    int maxSeq = 0;
                    HashMap<String, Integer> flows = new HashMap<>();
                    for(PacketInfo p: elements){
                        int seq = Integer.valueOf(p.get("TCP.seq"));
                        String flowKey = p.get("IP.src") + "_" + p.get("IP.dst");
                        if(seq > maxSeq){
                            maxSeq = seq;
                        }
                        else{
                            int cnt = flows.getOrDefault(flowKey, 0);
                            flows.put(flowKey, cnt + 1);
                        }
                    }

                    for(Map.Entry<String, Integer> e: flows.entrySet()){
                        if(e.getValue() > 10){
                            out.collect(e.getKey());
                        }
                    }
                });


        NetStream<String> stream3 = stream2
                .flatmap(new FlatMapFunction<String, String>() {
                    @Override
                    public void flatMap(String element, Collector<String> out) {
                        String dip = element.split("_")[1];
                        String result = env.get(local).sendProbe(dip);
                        out.collect(result);
                    }
                });

        env.execute("retransmission");
    }
}
