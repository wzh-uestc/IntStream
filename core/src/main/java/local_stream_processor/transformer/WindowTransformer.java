package local_stream_processor.transformer;

import local_stream_processor.Collector;
import local_stream_processor.Pipeline;
import local_stream_processor.functions.TransformFunction;
import org.apache.flink.api.java.functions.KeySelector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Function;

public class WindowTransformer<IN, OUT> extends AbstractTransformer<IN, OUT> {
    private Function<IN, Long> extractTimestampFunc;
    private TransformFunction<IN, OUT> transformFunc;

    private KeySelector<IN, String> keySelector;

    private int windowInterval = 0;
    private long cnt = 0;

    public void setExtractTimestampFunc(Function<IN, Long> extractTimestampFunc){
        this.extractTimestampFunc = extractTimestampFunc;
    }


    public void setExtractKeyFunc(KeySelector<IN, String> keySelector){
        this.keySelector = keySelector;
    }

    public void setTransformFunc(TransformFunction<IN, OUT> transformFunc){
        this.transformFunc = transformFunc;
    }

    public void setTimeWindow(int seconds){
        this.windowInterval = seconds;
    }

    public WindowTransformer(Pipeline<IN> in_pipe, Pipeline<OUT> out_pipe){
        super(in_pipe, out_pipe);
    }

    public void run(){
        /**
         * window process
         */
        WindowBuffer<IN> windowBuffer = new WindowBuffer<>(extractTimestampFunc, keySelector, windowInterval);

        while (true){
            IN t = in_pipe.pop();

            boolean flag = windowBuffer.add(t);

            if(!flag){
                //at the end of the window interval
                ArrayList<LinkedList<IN>> keyedStreams = windowBuffer.getKeyedStream();
                for(LinkedList<IN> keyedStream: keyedStreams){
                    this.transformFunc.process(keyedStream, new Collector<>(out_pipe));
                }

                windowBuffer.clear();
                windowBuffer.add(t);
            }

            cnt++;
        }
    }

    @Override
    public String toString() {
        return "Window Transformer";
    }

    @Override
    public long getCnt() {
        return cnt;
    }
}
