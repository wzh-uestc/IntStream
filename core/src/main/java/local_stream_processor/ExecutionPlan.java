package local_stream_processor;

import core.Log;
import local_stream_processor.sink.NetSink;
import local_stream_processor.source.NetSource;
import local_stream_processor.transformer.AbstractTransformer;

import java.util.ArrayList;
import java.util.List;

public class ExecutionPlan {
    private NetSource netSource;
    private NetSink<?> netSink;

    private ArrayList<Pipeline<?>> pipelines = new ArrayList<>();
    private ArrayList<AbstractTransformer<?,?>> transformers = new ArrayList<>();

    private String name;

    public ExecutionPlan(String name){
        this.name = name;
    }

    public void addNetSource(NetSource netSource){
        this.netSource = netSource;
    }

    public void addNetSink(NetSink<?> netSink){
        this.netSink = netSink;
    }

    public void addPipeline(Pipeline<?> pipeline){
        this.pipelines.add(pipeline);
    }

    public void addTransformer(AbstractTransformer<?,?> transformer){
        this.transformers.add(transformer);
    }

    public Pipeline<?> getFirstPipeline(){
        if(pipelines.size() > 0) {
            return pipelines.get(0);
        }
        return null;
    }

    public Pipeline<?> getLastPipeline(){
        if(pipelines.size() > 0) {
            return pipelines.get(pipelines.size() - 1);
        }
        return null;
    }

    public void execute(){
        if(netSource == null){
            Log.printError("Excution Plan [" + name + "]: Net Source is null");
        }
        else {
            this.netSource.start();
        }

        for(AbstractTransformer<?,?> transformer: this.transformers){
            transformer.start();
        }

        if(netSink == null){
            Log.printError("Excution Plan [" + name + "]: Net Sink is null");
        }
        else {
            this.netSink.start();
        }
    }

    public void print(){
        System.out.println("[Execution Plan]: " + name);
        System.out.println(netSource.toString());

        //System.out.println(pipelines.size());
        //System.out.println(transformers.size());

        int i = 0;
        for(;i < transformers.size();i++){
            System.out.println(pipelines.get(i).toString());
            System.out.println(transformers.get(i).toString());
        }

        System.out.println(pipelines.get(i).toString());

        System.out.println(netSink.toString());
    }

    public NetSource getNetSource() {
        return netSource;
    }

    public NetSink<?> getNetSink() {
        return netSink;
    }

    public ArrayList<AbstractTransformer<?, ?>> getTransformers() {
        return transformers;
    }
}
