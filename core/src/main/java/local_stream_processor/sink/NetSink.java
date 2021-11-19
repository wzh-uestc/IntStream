package local_stream_processor.sink;

import local_stream_processor.Pipeline;
import local_stream_processor.functions.SinkFunction;

public class NetSink<T> extends Thread{
    private Thread thread;
    private Pipeline<T> in_pipe;

    private long cnt = 0;

    private SinkFunction<T> sinkFunction;

    public NetSink(Pipeline<T> in_pipe, SinkFunction<T> sinkFunction){
        this.in_pipe = in_pipe;
        this.sinkFunction = sinkFunction;

    }

    public void run(){
        try {
            while(true){
                T t = this.in_pipe.pop();
                this.sinkFunction.process(t);
                cnt++;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void start(){
        if(thread == null){
            thread = new Thread(this, "NetSink");
            thread.start();
        }
    }

    public long getCnt(){
        return cnt;
    }

    @Override
    public String toString() {
        return sinkFunction.toString();
    }
}
