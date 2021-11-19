package local_stream_processor.transformer;

import local_stream_processor.Collector;
import local_stream_processor.Context;
import local_stream_processor.Pipeline;
import local_stream_processor.functions.FlatMapFunction;
import local_stream_processor.functions.ProcessElementFunction;

public class ProcessElementTransformer<IN, OUT> extends AbstractTransformer<IN, OUT> {
    private ProcessElementFunction<IN, OUT> processElementFunction;
    private long cnt = 0;

    public ProcessElementTransformer(Pipeline<IN> in_pipe, Pipeline<OUT> out_pipe, ProcessElementFunction<IN, OUT> processElementFunction){
        super(in_pipe, out_pipe);
        this.processElementFunction = processElementFunction;
    }

    public void run(){
        while(true){
            IN in = in_pipe.pop();
            this.processElementFunction.processElements(new Context<>(this), in, new Collector<>(out_pipe));
            cnt++;
        }
    }

    @Override
    public String toString() {
        return "ProcessElement Transformer";
    }

    @Override
    public long getCnt() {
        return cnt;
    }
}
