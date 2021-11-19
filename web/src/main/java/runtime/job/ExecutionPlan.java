package runtime.job;

import java.util.ArrayList;
import java.util.List;

public class ExecutionPlan {
    private ExecutionUnit source;
    private List<ExecutionUnit> transforms;
    private ExecutionUnit sink;

    public ExecutionPlan(){

    }

    public ExecutionPlan(String source, String transformer, String sink){
        this.source = new ExecutionUnit(source);
        this.transforms = new ArrayList<>();
        this.transforms.add(new ExecutionUnit(transformer));
        this.sink = new ExecutionUnit(sink);
    }

    public void setSink(ExecutionUnit sink) {
        this.sink = sink;
    }

    public ExecutionUnit getSink() {
        return sink;
    }

    public void setSource(ExecutionUnit source) {
        this.source = source;
    }

    public ExecutionUnit getSource() {
        return source;
    }

    public void setTransforms(List<ExecutionUnit> transforms) {
        this.transforms = transforms;
    }

    public List<ExecutionUnit> getTransforms() {
        return transforms;
    }

    @Override
    public String toString() {
        String res = "source: " + source.toString() + "\t";
        for(ExecutionUnit t: transforms){
            res += "transformer: " + t.toString() + "\t";
        }
        res += "sink: " + sink.toString();

        return res;
    }
}
