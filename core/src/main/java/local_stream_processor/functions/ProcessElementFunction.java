package local_stream_processor.functions;

import local_stream_processor.Collector;
import local_stream_processor.Context;

public interface ProcessElementFunction<IN, OUT> extends Function {
    void processElements(Context<IN, OUT> context, IN element, Collector<OUT> out);
}
