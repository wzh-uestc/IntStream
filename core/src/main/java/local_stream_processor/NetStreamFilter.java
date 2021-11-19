package local_stream_processor;

public class NetStreamFilter {
    private long startTimestamp;
    private long endTimestamp;

    public void setStartTimestamp(long startTimestamp){
        this.startTimestamp = startTimestamp;
    }

    public void setEndTimestamp(long endTimestamp){
        this.endTimestamp = endTimestamp;
    }
}
