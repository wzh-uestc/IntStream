package local_stream_processor.functions;

public interface FilterFunction<T> extends Function {
    boolean filter(T element);
}
