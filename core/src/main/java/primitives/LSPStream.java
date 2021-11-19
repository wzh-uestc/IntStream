package primitives;

import local_stream_processor.*;
import local_stream_processor.functions.*;
import local_stream_processor.sink.NetSink;
import local_stream_processor.transformer.*;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.util.function.Function;

public class LSPStream<T> implements NetStream<T> {
    private LocalEnvironment localEnvironment;

    private Function<T, Long> extractTimestampFunc = null;
    private KeySelector<T, String> keySelector = null;
    private int windowInterval = -1;

    private Pipeline<T> sourcePipeline;

    public LSPStream(LocalEnvironment localEnvironment){
        this.localEnvironment = localEnvironment;
        this.sourcePipeline = (Pipeline<T>) this.localEnvironment.getLastPipeline();
    }

    public LSPStream(LocalEnvironment localEnvironment, Pipeline<T> sourcePipeline){
        this.localEnvironment = localEnvironment;
        this.sourcePipeline = sourcePipeline;
    }

    public LSPStream<T> setExtractTimestampFunc(Function<T, Long> extractTimestampFunc){
        this.extractTimestampFunc = extractTimestampFunc;
        return this;
    }

    public LSPStream<T> setTimeWindow(int seconds){
        this.windowInterval = seconds;
        return this;
    }

    public LSPStream<T> setExtractKeyFunc(KeySelector<T, String> keySelector){
        this.keySelector = keySelector;
        return this;
    }

    public <R> LSPStream<R> transform(TransformFunction<T, R> transformFunc){
        Pipeline<T> in_pipe = this.sourcePipeline;
        Pipeline<R> out_pipe = new Pipeline<>();
        this.localEnvironment.addPipeline(out_pipe);

        WindowTransformer<T, R> transformer = new WindowTransformer<>(in_pipe, out_pipe);
        transformer.setTransformFunc(transformFunc);

        if(this.extractTimestampFunc != null){
            transformer.setExtractTimestampFunc(extractTimestampFunc);
        }
        if(this.windowInterval != -1){
            transformer.setTimeWindow(windowInterval);
        }

        /*
        if(this.extractKeyFunc != null){
            transformer.setExtractKeyFunc(extractKeyFunc);
        }
        */

        if(this.keySelector != null){
            transformer.setExtractKeyFunc(keySelector);
        }

        this.localEnvironment.addTransformer(transformer);

        LSPStream<R> ret = new LSPStream<>(this.localEnvironment, out_pipe);
        return ret;
    }

    public LSPStream<T> print(){
        return addSink(new SinkFunction<T>() {
            @Override
            public void process(T element) {
                System.out.println(element);
            }
        });
    }

    public LSPStream<T> addSink(SinkFunction<T> sinkFunction){
        Pipeline<T> in_pipe = this.sourcePipeline;

        NetSink<T> netSink = new NetSink<>(in_pipe, sinkFunction);

        this.localEnvironment.addNetSink(netSink);

        return this;
    }

    public <R> LSPStream<R> map(MapFunction<T, R> mapFunction){
        Pipeline<T> in_pipe = this.sourcePipeline;
        Pipeline<R> out_pipe = new Pipeline<>();
        this.localEnvironment.addPipeline(out_pipe);

        MapTransformer<T, R> transformer = new MapTransformer<>(in_pipe, out_pipe, mapFunction);

        this.localEnvironment.addTransformer(transformer);

        LSPStream<R> ret = new LSPStream<>(this.localEnvironment, out_pipe);
        return ret;
    }

    public <R> LSPStream<R> flatmap(FlatMapFunction<T, R> flatMapFunction){
        Pipeline<T> in_pipe = this.sourcePipeline;
        Pipeline<R> out_pipe = new Pipeline<>();
        this.localEnvironment.addPipeline(out_pipe);

        FlatMapTransformer<T, R> transformer = new FlatMapTransformer<>(in_pipe, out_pipe, flatMapFunction);

        this.localEnvironment.addTransformer(transformer);

        LSPStream<R> ret = new LSPStream<>(this.localEnvironment, out_pipe);
        return ret;
    }

    public <R> LSPStream<R> processElement(ProcessElementFunction<T, R> processElementFunction){
        Pipeline<T> in_pipe = this.sourcePipeline;
        Pipeline<R> out_pipe = new Pipeline<>();
        this.localEnvironment.addPipeline(out_pipe);

        ProcessElementTransformer<T, R> transformer = new ProcessElementTransformer<>(in_pipe, out_pipe, processElementFunction);

        this.localEnvironment.addTransformer(transformer);

        LSPStream<R> ret = new LSPStream<>(this.localEnvironment, out_pipe);
        return ret;
    }

    @Override
    public <R> NetStream<R> getSideOutput(Pipeline<R> sideOutputPipeline) {
        return new LSPStream<>(this.localEnvironment, sideOutputPipeline);
    }

    public LSPStream<T> filter(FilterFunction<T> filterFunction){
        Pipeline<T> in_pipe = this.sourcePipeline;
        Pipeline<T> out_pipe = new Pipeline<>();
        this.localEnvironment.addPipeline(out_pipe);

        FilterTransformer<T> transformer = new FilterTransformer<>(in_pipe, out_pipe, filterFunction);

        this.localEnvironment.addTransformer(transformer);

        LSPStream<T> ret = new LSPStream<>(this.localEnvironment, out_pipe);
        return ret;
    }

    /**
     * GSP interface
     * @param function
     * @param <R>
     * @return
     */
    @Deprecated
    public <R> LSPStream<R> transform(ProcessAllWindowFunction<T, R, TimeWindow> function){
        return null;
    }

    /**
     * GSP interface
     * @param timestampAndWatermarkAssigner
     * @return
     */
    @Deprecated
    public LSPStream<T> setExtractTimestampFunc(AssignerWithPeriodicWatermarks<T> timestampAndWatermarkAssigner){
        return null;
    }

    /**
     * GSP interface
     * @param sinkFunction
     * @return
     */
    @Deprecated
    public DataStreamSink<T> addSink(org.apache.flink.streaming.api.functions.sink.SinkFunction<T> sinkFunction){
        return null;
    }

    /**
     * GSP interface
     * @param <R>
     * @return
     */
    @Deprecated
    public <R> LSPStream<R> flatmap(org.apache.flink.api.common.functions.FlatMapFunction<T, R> flatMapFunction){
        return null;
    }
}
