package core;

import driver.stenographer_driver.StenographerDriver;
import local_stream_processor.sink.LSPKafkaProducer;
import local_stream_processor.functions.MapFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import primitives.NetStream;
import primitives.GSPStream;
import primitives.PacketInfo;

public class Test {
    public static void main(String[] args) throws Exception {

        /*
        Pipeline.addPipeline("source_pipe");
        Pipeline.addPipeline("pipe1");
        Pipeline.addPipeline("pipe2");

        NetSource pcapDriver = new NetSource("data/test1.pcap");
        Transformer<PacketInfo, Long> transformer = new Transformer<>("transformer_test", "source_pipe", "pipe1");
        //Transformer transformer2 = new Transformer("transformer_test", "pipe1", "pipe2");

        transformer.setExtractTimestampFunc(a->a.date);
        transformer.setTimeWindow(3);
        transformer.setTransformFunc(new Function<Iterable<PacketInfo>, Long>() {
            @Override
            public Long apply(Iterable<PacketInfo> packetInfos) {
                long t = 0;
                for(PacketInfo p: packetInfos){
                    t = p.date;
                }
                return t;
            }
        });



        pcapDriver.getNetStream();
        transformer.start();
        //transformer2.start();
        */


        /*
        ExecutionEnvironment env = new ExecutionEnvironment();
        env.addLocalEnvironment("local_1");

        NetStream<PacketInfo> stream = env.get("local_1").getNetStream("data/packetsinfo.csv");

        NetStream<Pair<String, Long>> stream2 = stream
                .setExtractTimestampFunc(a->Long.valueOf(a.get("time")))
                .setTimeWindow(3)
                .setExtractKeyFunc(a->a.get("IP.dst"))
                .transform((elements, out) -> {
                    long cnt = 0;
                    String dip = "";
                    for(PacketInfo pkt: elements){
                        if(pkt.get("TCP.flags").contains("S")){
                            cnt++;
                        }
                        dip = pkt.get("IP.dst");
                    }

                    if(cnt > 0) {
                        out.collect(new Pair<>(dip, cnt));
                    }
                });

        NetStream<String> stream3 = stream2
                .flatmap(new FlatMapFunction<Pair<String, Long>, String>() {
                    @Override
                    public void flatMap(Pair<String, Long> element, local_stream_processor.Collector<String> out) {
                        out.collect(element.getKey() + ": " + element.getValue());
                    }
                });

        NetStream<String> stream4 = stream3
                .filter(element -> element.equals("166.111.141.15: 2"));

        stream4.addSink(element -> System.out.println(element));
        //LSPKafkaProducer<Pair<String, Long>> lspKafkaProducer = new LSPKafkaProducer<>("localhost:9092", "test");

        //stream2.addSink(lspKafkaProducer);

        env.execute("local_1");
        */




        /*

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");

        StreamExecutionEnvironment ee = env.getGlobalEnvironment();

        FlinkKafkaConsumer<String> flinkKafkaConsumer = new FlinkKafkaConsumer<String>("test",new SimpleStringSchema(), properties);
        flinkKafkaConsumer.setStartFromEarliest();

        DataStream<String> stream3 = ee
                .addSource(flinkKafkaConsumer);

        stream3.print();

        ee.execute("dd");

*/



        //flink_test();

        //driver_test(args[0], Long.valueOf(args[1]), Long.valueOf(args[2]));

        minehunter_test();
    }

    public static void minehunter_test() throws Exception{
        /*
        ExecutionEnvironment env = new ExecutionEnvironment();
        env.addLocalEnvironment("local_1");

        NetStream<PacketInfo> stream = env.get("local_1").getNetStream(new StenographerDriver("/data/packets/", 1608270614, -1));

        NetStream<String> stream2 = stream
                .setExtractTimestampFunc(a->Long.valueOf(a.get("date")))
                .setTimeWindow(10)
                .transform((elements, out) -> {
                    for(PacketInfo p: elements){
                        out.collect(p.get("IP.src"));
                    }
                });

        LSPKafkaProducer<String> lspKafkaProducer = new LSPKafkaProducer<>("localhost:9092", "test");
        stream2.addSink(lspKafkaProducer);

        env.getLocalEnvironment("local_1").getLocalExecutionPlan().print();
        */


    }

    public static void driver_test(String path, long startTimestamp, long endTimestamp) throws Exception{
        System.out.println("driver test 1");
        StenographerDriver stenographerDriver = new StenographerDriver(path, startTimestamp, endTimestamp);

        PacketInfo packetInfo;
        while((packetInfo = stenographerDriver.getNextPacket()) != null){
            //System.out.println(packetInfo);
        }
    }

    public static void flink_test() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);


        DataStream<String> text = env.readTextFile("data/test.csv")
                .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<String>() {
                    @Override
                    public long extractAscendingTimestamp(String element) {
                        return Long.valueOf(element.split(",")[0]);
                    }
                });



        //text.map()

/*
        DataStream<String> test2 =  text
                .keyBy(a->a.split(",")[1])
                .timeWindowAll(Time.seconds(1))
                .process(new ProcessAllWindowFunction<String, String, TimeWindow>() {
                    @Override
                    public void process(Context context, Iterable<String> elements, Collector<String> out) throws Exception {
                        for(String e: elements){
                            System.out.println(e);
                            out.collect(e);
                        }
                    }
                });


    */


        NetStream<String> s = new GSPStream<>(text);

        NetStream<String> s2 = s
                .setExtractKeyFunc(a->a.split(",")[1])
                .setTimeWindow(1)
                .transform(new ProcessAllWindowFunction<String, String, TimeWindow>() {
                    @Override
                    public void process(Context context, Iterable<String> elements, Collector<String> out) throws Exception {
                        for(String e: elements){
                            System.out.println(e);
                            out.collect(e);
                        }
                    }
                });

        s2.map(new MapFunction<String, String>() {
            @Override
            public String map(String element) {
                return element;
            }
        });

        env.execute("test");
    }


    public void flink() throws Exception{
        /*
        String[] filter_fields = {"time", "Ethernet.src", "Ethernet.dst", "IP.src", "IP.dst", "TCP.sport", "TCP.dport", "TCP.flags"};


        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //event time
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("group.id", "test");

        FlinkKafkaConsumer<ObjectNode> consumer = new FlinkKafkaConsumer<ObjectNode>("test2", new JSONKeyValueDeserializationSchema(false), properties);
        consumer.setStartFromEarliest();

        DataStream<ObjectNode> stream = env.addSource(consumer);

        DataStream<PacketInfos> p1 =  stream.map((jsonNodes) -> {
            PacketInfos p = new PacketInfos(jsonNodes, filter_fields);
            return p;
        }).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<PacketInfos>() {
            @Override
            public long extractAscendingTimestamp(PacketInfos packetInfo) {
                return Long.valueOf(packetInfo.get("time")) * 1000;
            }
        });

        DataStream<PacketInfos> p2 = p1.filter((packet)->{
            return packet.get("TCP.flags").contains("S");
        });

        DataStream<Tuple2<String, Integer>> p3 = p2.map((packet)->{
            return new Tuple2<>(packet.get("IP.src"), 1);
        }).returns(Types.TUPLE(Types.STRING, Types.INT))
                .keyBy(0)
                .setTimeWindow(Time.seconds(1))
                .sum(1);

        DataStream<String> p4 = p3.filter((tuple)->{
            return tuple.f1 > 3;
        }).map((tuple)->{
            return tuple.f0;
        });

        //p2.print();
        p3.print();
        //p4.print();

        env.execute("main test");
        */
    }
}
