package local_stream_processor;

public class Collector<T> {
    private Pipeline<T> pipeline;

    public Collector(Pipeline<T> pipeline){
        this.pipeline = pipeline;
    }

    public void collect(T t){
        this.pipeline.push(t);
    }
}
