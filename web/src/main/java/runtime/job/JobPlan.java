package runtime.job;

import java.util.List;

public class JobPlan {
    private List<ExecutionPlan> lsp_plans;
    private ExecutionPlan gsp_plan;

    public void setGsp_plan(ExecutionPlan gsp_plan) {
        this.gsp_plan = gsp_plan;
    }

    public ExecutionPlan getGsp_plan() {
        return gsp_plan;
    }

    public void setLsp_plans(List<ExecutionPlan> lsp_plans) {
        this.lsp_plans = lsp_plans;
    }

    public List<ExecutionPlan> getLsp_plans() {
        return lsp_plans;
    }

    @Override
    public String toString() {
        String res = "";

        for(ExecutionPlan p: lsp_plans){
            res += p.toString() + "\n";
        }

        if(gsp_plan != null){
            res += gsp_plan.toString();
        }

        return res;
    }
}
