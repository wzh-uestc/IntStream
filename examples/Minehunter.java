package user_interface;

import driver.high_speed_driver.HighSpeedDriver;
import driver.high_speed_driver.HighSpeedRule;
import driver.stenographer_driver.StenographerDriver;
import core.ExecutionEnvironment;
import local_stream_processor.sink.LSPKafkaProducer;
import primitives.NetStream;
import primitives.PacketInfo;

import java.util.ArrayList;
import java.util.List;

public class Minehunter {
    public static boolean ipInSchool(String ip){
        String[] ipnums = ip.split("\\.");
        long ipNumber = Long.valueOf(ipnums[0]) * 16777216 + Long.valueOf(ipnums[1]) * 65536 + Long.valueOf(ipnums[2]) * 256 + Long.valueOf(ipnums[3]);

        long[] SchNet = {994181120L,994246655L,1694826496L,1694957567L,1786283008L,1786284031L,1994719232L,1994784767L,2792292352L,2792357887L,3081502720L,3081609215L,3397380608L,3397380863L,3396347392L,3396347647L};
        for (int i = 0;i < SchNet.length; i += 2)
        {
            if(SchNet[i] <= ipNumber && ipNumber <= SchNet[i+1]) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = new ExecutionEnvironment(args);
        String[] localEnvs = {"stenographer_s0_129", "stenographer_s0_132", "stenographer_s13_129", "stenographer_s13_132",
                "stenographer_s14_129", "stenographer_s14_132", "stenographer_s15_129", "stenographer_s15_132",
                "stenographer_s16_129", "stenographer_s16_132", "stenographer_s17_129", "stenographer_s17_132",
                "stenographer_s18_129", "stenographer_s18_132", "stenographer_s19_129", "stenographer_s19_132"};

        for(String local: localEnvs){
            env.addLocalEnvironment(local);
            NetStream<PacketInfo> stream = env.get(local).getNetStream(new StenographerDriver());

            NetStream<FlowTimeSeries> stream2 = stream
                    .filter(a->ipInSchool(a.get("IP.dst")))
                    .setExtractTimestampFunc(a->Long.valueOf(a.get("timestamp")))
                    .setExtractKeyFunc(a->a.get("IP.src") + "_" + a.get("IP.dst"))
                    .setTimeWindow(10)
                    .transform((elements, out) -> {
                        String sip = "", dip = "";
                        List<Long> times = new ArrayList<>();
                        for(PacketInfo p: elements){
                            sip = p.get("IP.src");
                            dip = p.get("IP.dst");
                            times.add(Long.valueOf(p.get("timestamp")));
                        }

                        FlowTimeSeries flowTimeSeries = new FlowTimeSeries(sip, dip, times);
                        out.collect(flowTimeSeries);
                    });

            LSPKafkaProducer<FlowTimeSeries> lspKafkaProducer = new LSPKafkaProducer<>("[2001:Da8:ff:212::41:21]:9092", "test");
            stream2.addSink(lspKafkaProducer);
        }

        env.execute("minehunter");
    }

    public static void main2(String[] args) throws Exception {
        ExecutionEnvironment env = new ExecutionEnvironment(args);
        String[] localEnvs = {"stenographer_s0_129", "stenographer_s0_132", "stenographer_s13_129", "stenographer_s13_132",
                "stenographer_s14_129", "stenographer_s14_132", "stenographer_s15_129", "stenographer_s15_132",
                "stenographer_s16_129", "stenographer_s16_132", "stenographer_s17_129", "stenographer_s17_132",
                "stenographer_s18_129", "stenographer_s18_132", "stenographer_s19_129", "stenographer_s19_132"};

        for(String local: localEnvs){
            env.addLocalEnvironment(local);

            NetStream<PacketInfo> stream = env.get(local).getNetStream(new StenographerDriver());

            NetStream<FlowTimeSeries> stream2 = stream
                    .filter(a->ipInSchool(a.get("IP.dst")))
                    .setExtractTimestampFunc(a->Long.valueOf(a.get("timestamp")))
                    .setExtractKeyFunc(a->a.get("IP.src") + "_" + a.get("IP.dst"))
                    .setTimeWindow(10)
                    .transform((elements, out) -> {
                        String sip = "", dip = "";
                        List<Long> times = new ArrayList<>();
                        for(PacketInfo p: elements){
                            sip = p.get("IP.src");
                            dip = p.get("IP.dst");
                            times.add(Long.valueOf(p.get("timestamp")));
                        }

                        FlowTimeSeries flowTimeSeries = new FlowTimeSeries(sip, dip, times);
                        out.collect(flowTimeSeries);
                    });

            LSPKafkaProducer<FlowTimeSeries> lspKafkaProducer = new LSPKafkaProducer<>("[2001:Da8:ff:212::41:21]:9092", "test");
            stream2.addSink(lspKafkaProducer);
        }

        env.execute("minehunter");
    }
}

