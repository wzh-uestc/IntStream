package local_stream_processor.functions;

import local_stream_processor.Collector;

public interface FlatMapFunction<IN, OUT> extends Function {
    void flatMap(IN element, Collector<OUT> out);
}
