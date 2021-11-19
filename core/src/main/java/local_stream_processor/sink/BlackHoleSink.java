package local_stream_processor.sink;

import local_stream_processor.functions.SinkFunction;

public class BlackHoleSink<T> implements SinkFunction<T> {

    @Override
    public void process(T element) {
        //do nothing
    }
}
