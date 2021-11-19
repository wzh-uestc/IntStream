package active_probe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PingDriver {
    public PingResult sendPing(String dst, int cnt) throws Exception {
        String cmd = "ping " + dst + " -c " + String.valueOf(cnt);

        Process p = Runtime.getRuntime().exec(cmd);

        InputStream is = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        p.waitFor();

        if(p.exitValue() != 0){
            System.out.println("PING ERROR");
        }

        String line = null;
        int transCnt = 0, recvCnt = 0;
        ArrayList<Double> rtts = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            if(line.contains("icmp_seq=")){
                double rtt = Double.parseDouble(line.split("time=")[1].split(" ms")[0]);
                rtts.add(rtt);
            }
            else if(line.contains("packets transmitted")){
                transCnt = Integer.parseInt(line.split(" packets transmitted")[0]);
                recvCnt = Integer.parseInt(line.split("packets transmitted, ")[1].split(" packets received")[0]);
            }
        }
        return new PingResult(transCnt, recvCnt, rtts);
    }
}
