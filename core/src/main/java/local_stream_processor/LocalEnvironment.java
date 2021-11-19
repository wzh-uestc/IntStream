package local_stream_processor;

import driver.AbstractDriver;
import local_stream_processor.sink.NetSink;
import local_stream_processor.source.NetSource;
import local_stream_processor.transformer.AbstractTransformer;
import primitives.LSPStream;
import primitives.PacketInfo;

import java.io.IOException;

public class LocalEnvironment {
    private String name;
    private ExecutionPlan localExecutionPlan;

    public LocalEnvironment(String name){
        this.name = name;
        this.localExecutionPlan = new ExecutionPlan(name);
    }

    public LSPStream<PacketInfo> getNetStream(AbstractDriver driver) throws IOException {
        //create the first pipeline
        Pipeline<PacketInfo> pipeline = new Pipeline<>();
        this.localExecutionPlan.addPipeline(pipeline);

        this.localExecutionPlan.addNetSource(new NetSource(this, driver));

        LSPStream<PacketInfo> ret = new LSPStream<>(this, pipeline);

        return ret;
    }

    public Pipeline<?> getFirstPipeline(){
        return this.localExecutionPlan.getFirstPipeline();
    }

    public Pipeline<?> getLastPipeline(){
        return this.localExecutionPlan.getLastPipeline();
    }

    public void addPipeline(Pipeline<?> pipeline){
        this.localExecutionPlan.addPipeline(pipeline);
    }

    public void addTransformer(AbstractTransformer<?,?> transformer){
        this.localExecutionPlan.addTransformer(transformer);
    }

    public void addNetSink(NetSink<?> netSink){
        this.localExecutionPlan.addNetSink(netSink);
    }

    public void execute(){
        this.localExecutionPlan.execute();
    }

    public ExecutionPlan getLocalExecutionPlan(){
        return this.localExecutionPlan;
    }

    public String sendProbe(String dip){
        return "";
    }
}
