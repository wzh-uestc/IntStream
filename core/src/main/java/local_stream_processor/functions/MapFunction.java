package local_stream_processor.functions;

import java.io.Serializable;

public interface MapFunction<IN, OUT> extends Function {
    OUT map(IN element);
}
