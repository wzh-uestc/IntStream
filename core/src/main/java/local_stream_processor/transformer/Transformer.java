package local_stream_processor.transformer;

import local_stream_processor.Collector;
import local_stream_processor.Pipeline;
import local_stream_processor.functions.TransformFunction;
import org.apache.flink.api.java.functions.KeySelector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Function;

public class Transformer <T, R> extends Thread {
    private Thread thread = null;
    private String threadName;
    private Pipeline<T> in_pipe = null;
    private Pipeline<R> out_pipe = null;

    private Function<T, Long> extractTimestampFunc;
    private TransformFunction<T, R> transformFunc;

    private KeySelector<T, String> keySelector;

    private int windowInterval = 0;

    public void setExtractTimestampFunc(Function<T, Long> extractTimestampFunc){
        this.extractTimestampFunc = extractTimestampFunc;
    }

    /*
    public void setExtractKeyFunc(Function<T, String> extractKeyFunc){
        this.extractKeyFunc = extractKeyFunc;
    }
    */

    public void setExtractKeyFunc(KeySelector<T, String> keySelector){
        this.keySelector = keySelector;
    }

    public void setTransformFunc(TransformFunction<T, R> transformFunc){
        this.transformFunc = transformFunc;
    }

    public void setTimeWindow(int seconds){
        this.windowInterval = seconds;
    }

    public Transformer(String threadName, Pipeline<T> in_pipe, Pipeline<R> out_pipe){
        this.threadName = threadName;
        this.in_pipe = in_pipe;
        this.out_pipe = out_pipe;
    }

    public void run(){
        /**
         * window process
         */
        WindowBuffer<T> windowBuffer = new WindowBuffer<>(extractTimestampFunc, keySelector, windowInterval);

        while (true){
            T t = in_pipe.pop();

            boolean flag = windowBuffer.add(t);

            if(!flag){
                //at the end of the window interval
                ArrayList<LinkedList<T>> keyedStreams = windowBuffer.getKeyedStream();
                for(LinkedList<T> keyedStream: keyedStreams){
                    this.transformFunc.process(keyedStream, new Collector<>(out_pipe));
                }

                windowBuffer.clear();
                windowBuffer.add(t);
            }
        }
    }

    public void start(){
        if(thread == null){
            thread = new Thread(this, threadName);
            thread.start();
        }
    }
}
