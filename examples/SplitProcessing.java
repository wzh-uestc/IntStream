
package user_interface;

import core.ExecutionEnvironment;
import driver.pcap_driver.PcapDriver;
import driver.stenographer_driver.StenographerDriver;
import local_stream_processor.Pipeline;
import local_stream_processor.sink.BlackHoleSink;
import local_stream_processor.sink.LSPKafkaProducer;
import primitives.NetStream;
import primitives.PacketInfo;

public class SplitProcessing {

    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = new ExecutionEnvironment();

        String local = "local";
        env.addLocalEnvironment(local);

        NetStream<PacketInfo> stream = env.get(local).getNetStream(new StenographerDriver());

        Pipeline<String> pipeline = new Pipeline();

        NetStream<String> stream2 = stream.processElement(((context, element, out) -> {
            if(element.get("IP.protocol").equals("TCP")){
                context.sideOutput(pipeline, element.toString());
            }
            else if(element.get("IP.protocol").equals("UDP")){
                out.collect(element.toString());
            }
        }));


        NetStream<String> stream3 = stream2.getSideOutput(pipeline);
        stream3.map(a -> {
            System.out.println(a);
            return "";
        }).addSink(new BlackHoleSink<>());

        env.execute("Split processing");
    }
}
