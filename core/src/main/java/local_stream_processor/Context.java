package local_stream_processor;

import local_stream_processor.transformer.AbstractTransformer;

public class Context<IN, OUT> {
    private AbstractTransformer<IN, OUT> abstractTransformer;

    public Context(AbstractTransformer<IN, OUT> abstractTransformer){
        this.abstractTransformer = abstractTransformer;
    }

    public void sideOutput(Pipeline<OUT> sideOutputPipeline, OUT out){
        this.abstractTransformer.sideOutput(sideOutputPipeline, out);
    }
}
