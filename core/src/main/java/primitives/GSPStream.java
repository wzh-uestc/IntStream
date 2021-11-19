package primitives;

import local_stream_processor.Pipeline;
import local_stream_processor.functions.*;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.lang.reflect.ParameterizedType;
import java.util.function.Function;

public class GSPStream<T> extends DataStream<T> implements NetStream<T> {
    private int window_interval;

    public GSPStream(DataStream<T> dataStream){
        super(dataStream.getExecutionEnvironment(), dataStream.getTransformation());
    }


    public GSPStream<T> setTimeWindow(int seconds){
        this.window_interval = seconds;
        return this;
    }

    public <R> GSPStream<R> transform(ProcessAllWindowFunction<T, R, TimeWindow> function){
        DataStream<R> res = super.timeWindowAll(Time.seconds(this.window_interval))
                .process(function);

        return new GSPStream<>(res);
    }

    /*
    public <K> GSPStream<T> setExtractKeyFunc(KeySelector<T, K>key){
        DataStream<T> res = super.keyBy(key);
        return new GSPStream<>(res);
    }
    */

    public GSPStream<T> setExtractKeyFunc(KeySelector<T, String> keySelector){
        DataStream<T> res = super.keyBy(keySelector);
        return new GSPStream<>(res);
    }

    public GSPStream<T> setExtractTimestampFunc(AssignerWithPeriodicWatermarks<T> timestampAndWatermarkAssigner){
        DataStream<T> res = super.assignTimestampsAndWatermarks(timestampAndWatermarkAssigner);
        return new GSPStream<>(res);
    }

    public DataStreamSink<T> addSink(org.apache.flink.streaming.api.functions.sink.SinkFunction<T> sinkFunction){
        DataStreamSink<T> res = super.addSink(sinkFunction);
        return res;
    }

    public <R> GSPStream<R> map(MapFunction<T, R> mapFunction){
        /**
         * get Class type infomation of R from the interface mapFunction:
         * (mapFunction.getClass().getGenericInterfaces()[0]).getActualTypeArguments()
         *
         * Note that it cannot get class type infomation from lambda expressions
         */

        //System.out.println(((ParameterizedType)mapFunction.getClass().getGenericInterfaces()[0]));
        DataStream<R> res = super.map(new org.apache.flink.api.common.functions.MapFunction<T, R>() {
            @Override
            public R map(T t) throws Exception {
                R r = mapFunction.map(t);
                return r;
            }
        }).returns((Class<R>)(((ParameterizedType)mapFunction.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1]));

        return new GSPStream<>(res);
    }

    public <R> GSPStream<R> flatmap(org.apache.flink.api.common.functions.FlatMapFunction<T, R> flatMapFunction){
        DataStream<R> res = super.flatMap(flatMapFunction);

        return new GSPStream<>(res);
    }

    public GSPStream<T> filter(FilterFunction<T> filterFunction){
        DataStream<T> res = super.filter(new org.apache.flink.api.common.functions.FilterFunction<T>() {
            @Override
            public boolean filter(T t) throws Exception {
                return filterFunction.filter(t);
            }
        }).returns((Class<T>)(((ParameterizedType)filterFunction.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]));

        return new GSPStream<>(res);
    }

    /**
     * LSP interface
     * @param transformFunc
     * @param <R>
     * @return
     */
    @Deprecated
    public <R> GSPStream<R> transform(TransformFunction<T, R> transformFunc){
        return null;
    }


    /**
     * LSP interface
     * @param extractTimestampFunc
     * @return
     */
    @Deprecated
    public GSPStream<T> setExtractTimestampFunc(Function<T, Long> extractTimestampFunc){
        return null;
    }

    /**
     * LSP interface
     * @param sinkFunction
     * @return
     */
    @Deprecated
    public GSPStream<T> addSink(SinkFunction<T> sinkFunction){
        return null;
    }


    /**
     * LSP interface
     * @param flatMapFunction
     * @param <R>
     * @return
     */
    @Deprecated
    public <R> GSPStream<R> flatmap(FlatMapFunction<T, R> flatMapFunction){
        return null;
    }


    @Deprecated
    public <R> GSPStream<R> processElement(ProcessElementFunction<T, R> processElementFunction){
        return null;
    }

    @Deprecated
    public <R> GSPStream<R> getSideOutput(Pipeline<R> sideOutputPipeline){
        return null;
    }
}
