package user_interface;

import java.util.ArrayList;
import java.util.List;

public class FlowTimeSeries {
    public String sip;
    public String dip;
    public List<Long> timeSeries;

    public FlowTimeSeries(String sip, String dip, List<Long> timeSeries){
        this.sip = sip;
        this.dip = dip;
        this.timeSeries = new ArrayList<>();
        this.timeSeries.addAll(timeSeries);
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer(sip + ", " + dip);

        for(long t: timeSeries){
            stringBuffer.append(", " + t);
        }

        return stringBuffer.toString();
    }
}
