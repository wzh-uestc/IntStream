package local_stream_processor.transformer;

import local_stream_processor.Pipeline;
import local_stream_processor.functions.MapFunction;

public class MapTransformer<IN, OUT> extends AbstractTransformer<IN, OUT> {
    private MapFunction<IN, OUT> mapFunction;
    private long cnt = 0;

    public MapTransformer(Pipeline<IN> in_pipe, Pipeline<OUT> out_pipe, MapFunction<IN, OUT> mapFunction){
        super(in_pipe, out_pipe);
        this.mapFunction = mapFunction;
    }

    public void run(){
        while(true){
            IN in = in_pipe.pop();
            OUT out = this.mapFunction.map(in);
            out_pipe.push(out);
            cnt++;
        }
    }

    @Override
    public String toString() {
        return "Map Transformer";
    }

    @Override
    public long getCnt() {
        return cnt;
    }
}
