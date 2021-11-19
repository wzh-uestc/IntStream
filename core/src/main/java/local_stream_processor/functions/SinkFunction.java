package local_stream_processor.functions;

public interface SinkFunction<IN> extends Function {
    void process(IN element);
}
