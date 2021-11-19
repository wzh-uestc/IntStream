package core;

public class RuntimeStatistics {
    public String date;
    public String job_name;
    public String lsp_name;
    public long source_cnt;
    public long sink_cnt;
    public long interval_source_cnt;
    public long interval_sink_cnt;
    public double cpu_utilization;
    public long memory_usage;

    public RuntimeStatistics(String date, String job_name, String lsp_name, long source_cnt, long sink_cnt, long interval_source_cnt, long interval_sink_cnt, double cpu_utilization, long memory_usage){
        this.date = date;
        this.job_name = job_name;
        this.lsp_name = lsp_name;
        this.source_cnt = source_cnt;
        this.sink_cnt = sink_cnt;
        this.interval_source_cnt = interval_source_cnt;
        this.interval_sink_cnt = interval_sink_cnt;
        this.cpu_utilization = cpu_utilization;
        this.memory_usage = memory_usage;
    }

    public void setCpu_utilization(double cpu_utilization) {
        this.cpu_utilization = cpu_utilization;
    }

    public double getCpu_utilization() {
        return cpu_utilization;
    }

    public void setJob_name(String job_name) {
        this.job_name = job_name;
    }

    public String getJob_name() {
        return job_name;
    }

    public void setLsp_name(String lsp_name) {
        this.lsp_name = lsp_name;
    }

    public String getLsp_name() {
        return lsp_name;
    }

    public void setMemory_usage(long memory_usage) {
        this.memory_usage = memory_usage;
    }

    public long getMemory_usage() {
        return memory_usage;
    }

    public void setSink_cnt(long sink_cnt) {
        this.sink_cnt = sink_cnt;
    }

    public long getSink_cnt() {
        return sink_cnt;
    }

    public void setSource_cnt(long source_cnt) {
        this.source_cnt = source_cnt;
    }

    public long getSource_cnt() {
        return source_cnt;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setInterval_sink_cnt(long interval_sink_cnt) {
        this.interval_sink_cnt = interval_sink_cnt;
    }

    public long getInterval_sink_cnt() {
        return interval_sink_cnt;
    }

    public void setInterval_source_cnt(long interval_source_cnt) {
        this.interval_source_cnt = interval_source_cnt;
    }

    public long getInterval_source_cnt() {
        return interval_source_cnt;
    }
}
