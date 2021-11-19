package local_stream_processor.functions;

import local_stream_processor.Collector;
import local_stream_processor.Context;

public interface TransformFunction<IN,OUT> extends Function{
    void process(Iterable<IN> elements, Collector<OUT> out);
}