package local_stream_processor.source;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import driver.AbstractDriver;
import local_stream_processor.LocalEnvironment;
import local_stream_processor.Pipeline;
import primitives.PacketInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class NetSource extends Thread {
    private LocalEnvironment localEnvironment;
    private Thread thread;
    private long cnt = 0;

    private AbstractDriver driver;

    public NetSource(LocalEnvironment localEnvironment, AbstractDriver driver) throws IOException {
        this.localEnvironment = localEnvironment;
        this.driver = driver;
    }


    public void run(){
        try {
            Pipeline<PacketInfo> pipeline = (Pipeline<PacketInfo>) this.localEnvironment.getFirstPipeline();

            /*
            String line = "";
            while((line = this.bufferedReader.readLine()) != null){
                String[] tmp = line.split(",");
                PacketInfo packetInfo = new PacketInfo();
                for(int i = 0;i < tmp.length;i++){
                    packetInfo.set(this.fields[i], tmp[i]);
                }
                pipeline.push(packetInfo);
            }
            */

            PacketInfo packetInfo;
            while((packetInfo = this.driver.getNextPacket()) != null){
                pipeline.push(packetInfo);
                cnt++;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public long getCnt(){
        return cnt;
    }

    public void start(){
        if(thread == null){
            thread = new Thread(this, "NetSource");
            thread.start();
        }
    }

    @Override
    public String toString() {
        return driver.toString();
    }
}
