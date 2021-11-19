package local_stream_processor.transformer;

import local_stream_processor.Pipeline;
import primitives.NetStream;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractTransformer<IN, OUT> extends Thread {
    private Thread thread = null;
    protected Pipeline<IN> in_pipe;
    protected Pipeline<OUT> out_pipe;

    public AbstractTransformer(Pipeline<IN> in_pipe, Pipeline<OUT> out_pipe){
        this.in_pipe = in_pipe;
        this.out_pipe = out_pipe;
    }

    public abstract void run();

    public abstract long getCnt();

    public void sideOutput(Pipeline<OUT> sideOutputPipeline, OUT out){
        sideOutputPipeline.push(out);
    }

    public void start(){
        if(thread == null){
            thread = new Thread(this);
            thread.start();
        }
    }

}
