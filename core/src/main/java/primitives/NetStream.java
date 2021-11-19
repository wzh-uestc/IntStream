package primitives;

import local_stream_processor.Pipeline;
import local_stream_processor.functions.*;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.util.function.Function;

public interface NetStream<T> {
    /*
    public
     */
    NetStream<T> setTimeWindow(int seconds);
    NetStream<T> setExtractKeyFunc(KeySelector<T, String> keySelector);

    <R> NetStream<R> map(MapFunction<T, R> mapFunction);

    NetStream<T> filter(FilterFunction<T> filterFunction);


    /*
    GSP
     */
    <R> NetStream<R> transform(ProcessAllWindowFunction<T, R, TimeWindow> function);

    NetStream<T> setExtractTimestampFunc(AssignerWithPeriodicWatermarks<T> timestampAndWatermarkAssigner);

   // <K> NetStream<T> setExtractKeyFunc(KeySelector<T, K> key);

    DataStreamSink<T> addSink(org.apache.flink.streaming.api.functions.sink.SinkFunction<T> sinkFunction);

    <R> NetStream<R> flatmap(org.apache.flink.api.common.functions.FlatMapFunction<T, R> flatMapFunction);


    /*
    LSP
     */
    <R> NetStream<R> transform(TransformFunction<T, R> transformFunc);

    NetStream<T> setExtractTimestampFunc(Function<T, Long> extractTimestampFunc);

    NetStream<T> addSink(SinkFunction<T> sinkFunction);

    <R> NetStream<R> flatmap(FlatMapFunction<T, R> flatMapFunction);

    <R> NetStream<R> processElement(ProcessElementFunction<T, R> processElementFunction);

    <R> NetStream<R> getSideOutput(Pipeline<R> sideOutputPipeline);

}
