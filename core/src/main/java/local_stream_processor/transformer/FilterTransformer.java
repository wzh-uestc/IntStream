package local_stream_processor.transformer;

import local_stream_processor.Pipeline;
import local_stream_processor.functions.FilterFunction;

public class FilterTransformer<T> extends AbstractTransformer<T, T> {
    private FilterFunction<T> filterFunction;
    private long cnt = 0;

    public FilterTransformer(Pipeline<T> in_pipe, Pipeline<T> out_pipe, FilterFunction<T> filterFunction){
        super(in_pipe, out_pipe);
        this.filterFunction = filterFunction;
    }

    public void run(){
        while (true){
            T t = in_pipe.pop();
            if(this.filterFunction.filter(t)){
                out_pipe.push(t);
            }
            cnt++;
        }
    }

    @Override
    public String toString() {
        return "Filter Transformer";
    }

    @Override
    public long getCnt() {
        return cnt;
    }
}
