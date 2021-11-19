package primitives;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

public class Sketch {
    private int H;
    private int K;
    private int p;
    private int a[] = {34,96,5,13,442,17,987,33};
    private int b[] = {26,77,344,563,2,91,237,8};
    private long timestamp = 0;

    public int data[][];

    public Sketch(){
        this.H = 8;
        this.K = 4096;
        this.p = 4093;

        this.data = new int[this.H][this.K];
    }

    public Sketch(Iterable<Integer> data, long timestamp){
        this.H = 8;
        this.K = 4096;
        this.p = 4093;

        this.data = new int[this.H][this.K];
        List<Integer> tmp = new ArrayList<>(this.H * this.K);
        for(Integer t:data){
            tmp.add(t);
        }

        int t = 0;
        for(int i = 0;i < this.H;i++){
            for(int j = 0;j < this.K;j++){
                this.data[i][j] = tmp.get(t++);
            }
        }
        this.timestamp = timestamp;
    }

    public void fit(long key, int value){
        for(int i = 0;i < this.H;i++){
            int hashed = (int)((key * this.a[i] + this.b[i]) % this.p);
            this.data[i][hashed] += value;
        }
    }

    public void setTimestamp(long timestamp){
        //date of the first packet belong to this sketch
        if(this.timestamp == 0 || timestamp < this.timestamp){
            this.timestamp = timestamp;
        }
    }

    public long getTimestamp(){
        return timestamp;
    }

    public List<Integer> getSketchData(){
        List<Integer> res = new ArrayList<>(this.H * this.K);
        for(int i = 0;i < this.H;i++){
            for(int j = 0;j < this.K;j++){
                res.add(this.data[i][j]);
            }
        }

        return res;
    }

    public static DataStream<Sketch> generate(DataStream<PacketInfos> stream, int window_time){
        //long key = function.apply()

        //<date, key, sum>
        DataStream<Tuple3<Long, Long, Integer>> d1 = stream.map((packet) -> {
            long timestamp = Long.valueOf(packet.get("time"));
            long key = PacketInfos.get_key(packet);
            return new Tuple3<>(timestamp, key, 1);
        })
                .returns(Types.TUPLE(Types.LONG, Types.LONG, Types.INT))
                .keyBy(1)
                .timeWindow(Time.seconds(window_time))
                .sum(2);

        //get one sketch per T
        DataStream<Sketch> d2 = d1
                .windowAll(TumblingEventTimeWindows.of(Time.seconds(window_time)))
                .process(new ProcessAllWindowFunction<Tuple3<Long, Long, Integer>, Sketch, TimeWindow>() {
                    @Override
                    public void process(Context context, Iterable<Tuple3<Long, Long, Integer>> iterable, Collector<Sketch> out) throws Exception {
                        Sketch sketch = new Sketch();
                        for(Tuple3<Long, Long, Integer> t:iterable){
                            sketch.setTimestamp(t.f0);
                            sketch.fit(t.f1, t.f2);
                        }

                        out.collect(sketch);
                    }
                });

        return d2;
    }
}
