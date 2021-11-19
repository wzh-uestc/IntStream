package runtime.job;

import java.util.UUID;

public class JobSummary {
    private String id;
    private String name;
    private long startTimestamp;
    private long endTimestamp;
    private String status;

    public JobSummary(String id, String name, long startTimestamp, long endTimestamp, String status){
        this.id = id;
        this.name = name;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.status = status;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setStartTimestamp(long startTimestamp){
        this.startTimestamp = startTimestamp;
    }

    public long getStartTimestamp(){
        return this.startTimestamp;
    }

    public void setEndTimestamp(long endTimestamp){
        this.endTimestamp = endTimestamp;
    }

    public long getEndTimestamp(){
        return this.endTimestamp;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return this.status;
    }

    @Override
    public String toString() {
        return this.id + " " + this.name + " " + this.startTimestamp + " " + this.endTimestamp + " " + this.status;
    }
}
