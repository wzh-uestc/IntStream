package runtime.job;

public class JobInfo {
    private String id;
    private String name;
    private long start_timestamp;
    private long end_timestamp;
    private String status;
    private JobPlan job_plan;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setEnd_timestamp(long end_timestamp) {
        this.end_timestamp = end_timestamp;
    }

    public long getEnd_timestamp() {
        return end_timestamp;
    }

    public void setJob_plan(JobPlan job_plan) {
        this.job_plan = job_plan;
    }

    public JobPlan getJob_plan() {
        return job_plan;
    }

    public void setStart_timestamp(long start_timestamp) {
        this.start_timestamp = start_timestamp;
    }

    public long getStart_timestamp() {
        return start_timestamp;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
