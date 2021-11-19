package local_stream_processor.transformer;

import local_stream_processor.Collector;
import local_stream_processor.Pipeline;
import local_stream_processor.functions.FlatMapFunction;

public class FlatMapTransformer<IN, OUT> extends AbstractTransformer<IN, OUT> {
    private FlatMapFunction<IN, OUT> flatMapFunction;
    private long cnt = 0;

    public FlatMapTransformer(Pipeline<IN> in_pipe, Pipeline<OUT> out_pipe, FlatMapFunction<IN, OUT> flatMapFunction){
        super(in_pipe, out_pipe);
        this.flatMapFunction = flatMapFunction;
    }

    public void run(){
        while(true){
            IN in = in_pipe.pop();
            this.flatMapFunction.flatMap(in, new Collector<>(out_pipe));
            cnt++;
        }
    }

    @Override
    public String toString() {
        return "FlatMap Transformer";
    }

    @Override
    public long getCnt() {
        return cnt;
    }
}
