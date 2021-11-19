package active_probe;

import java.util.ArrayList;

public class PingResult {
    public int transCnt;
    public int recvCnt;

    public ArrayList<Double> rtts;

    public PingResult(int transCnt, int recvCnt, ArrayList<Double> rtts){
        this.transCnt = transCnt;
        this.recvCnt = recvCnt;
        this.rtts = new ArrayList<>(rtts);
    }

    public double getAvgRtt(){
        if(rtts.size() == 0) return 0.0;

        double sum = 0.0;
        for(double rtt: rtts){
            sum += rtt;
        }
        return sum / rtts.size();
    }

    public double getLossRate(){
        return 1.0 - ((double)recvCnt / transCnt);
    }

    @Override
    public String toString() {
        String res = "";
        res += transCnt + " packets transmitted\n";
        res += recvCnt + " packets received\n";
        res += rtts.toString() + "\n";
        res += "Average RTT: " + getAvgRtt() + "\n";
        res += "Loss Rate: " + getLossRate() + "\n";

        return res;
    }
}
