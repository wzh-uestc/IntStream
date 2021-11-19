package driver.stenographer_driver;

import driver.AbstractDriver;
import driver.pcap_driver.PacketHeader;
import primitives.PacketInfo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;

import static driver.pcap_driver.PacketParse.*;

public class StenographerDriver implements AbstractDriver {
    private BufferedInputStream bufferedInputStream = null;
    private int size = 1024 * 1024 * 1024; // 1024 M

    private byte[] buf = new byte[1024 * 1024];
    private int read_len = -1;

    private int current_packet_num = 0;
    private int packet_num_total = 0;
    private int start = 48;

    private String dirPath;
    private long startTimestamp = -1;
    private long endTimestamp = -1;
    private long currentTimestamp = -1;

    private String lastStenographerFile = null;

    /**
     * bufferedInputStream read to fill buf
     * if current stenographer file is done, try to read next stenographer file
     *
     * @throws IOException
     */
    private int readBuf() throws IOException, InterruptedException{
        if(read_len == -1){
            //current stenographer file done
            String nextStenographerFile;
            while((nextStenographerFile = getNextStenographerFile()) == null){
                System.out.println("WAIT 30 seconds");
                Thread.sleep(30 * 1000);
            }

            //if next stenographer file < endTimestamp
            if(endTimestamp == -1 || Long.valueOf(nextStenographerFile) < endTimestamp){
                //read next stenographer file
                this.bufferedInputStream = new BufferedInputStream(new FileInputStream(dirPath + nextStenographerFile), size);

                //update currentTimestamp
                if(startTimestamp != -1) {
                    this.currentTimestamp = Long.valueOf(nextStenographerFile);
                }
                else{
                    this.currentTimestamp = getRealTimeDetectTimestamp();
                }

                System.out.println("READ File: " + dirPath + nextStenographerFile);
            }
        }


        if(bufferedInputStream == null){
            return -1;
        }

        this.read_len = bufferedInputStream.read(buf,0, buf.length);

        this.current_packet_num = 0;

        byte[] packet_num_byte = new byte[4];
        System.arraycopy(buf, 12, packet_num_byte, 0, 4);
        this.packet_num_total = bytesToInt(packet_num_byte,0,true);

        this.start = 48;

        return this.read_len;
    }

    /**
     * real time detect timestamp = now - 1 hour
     * @return
     */
    private long getRealTimeDetectTimestamp(){
        return System.currentTimeMillis() * 1000 - 3600L * 1000 * 1000;
    }

    /**
     * not given startTimestamp and endTimestamp, real-time detect
     */
    public StenographerDriver(){
        this.dirPath = "/data/packets/";
        this.currentTimestamp = getRealTimeDetectTimestamp();
    }

    public StenographerDriver(long startTimestamp, long endTimestamp) throws Exception{
        this.dirPath = "/data/packets/";
        this.startTimestamp = startTimestamp * 1000 * 1000;
        this.currentTimestamp = startTimestamp * 1000 * 1000;

        if(endTimestamp > 0) {
            this.endTimestamp = endTimestamp * 1000 * 1000;
        }
    }

    public StenographerDriver(String dirPath, long startTimestamp, long endTimestamp) throws Exception{
        this.dirPath = dirPath;
        this.startTimestamp = startTimestamp * 1000 * 1000;
        this.currentTimestamp = startTimestamp * 1000 * 1000;

        if(endTimestamp > 0) {
            this.endTimestamp = endTimestamp * 1000 * 1000;
        }
    }

    private String getNextStenographerFile(){
        File dir = new File(this.dirPath);

        ArrayList<Long> time_list = new ArrayList<Long>();
        if(dir.listFiles()!=null){
            for(File file :dir.listFiles()){
                if(file.getName().length()==16){
                    time_list.add(Long.valueOf(file.getName()));
                }
            }
        }
        Collections.sort(time_list);

        for(long t: time_list){
            if(t > this.currentTimestamp && (lastStenographerFile == null || t > Long.valueOf(lastStenographerFile))){
                String file = String.valueOf(t);
                lastStenographerFile = file;
                return file;
            }
        }
        return null;
    }

    public PacketInfo getNextPacket() throws Exception{
        if(read_len == -1){
            int len = readBuf();
            if(len == -1){
                return null;
            }
        }

        PacketInfo packetInfo = null;

        //get tp_next_offset
        byte[] tp_next_offset =new byte[4];
        System.arraycopy(buf, start, tp_next_offset, 0, 4);

        //get packet header
        PacketHeader packet = new PacketHeader();
        System.arraycopy(buf, start+4, packet.second, 0, 4);
        System.arraycopy(buf, start+4+4, packet.microsecond, 0, 4);
        int time_tmp;
        time_tmp = bytesToInt(packet.microsecond,0,true);
        time_tmp = time_tmp/1000;
        packet.microsecond = intToLittleEndian(time_tmp);
        System.arraycopy(buf, start+4+4+4, packet.caplen, 0, 4);
        System.arraycopy(buf, start+4+4+4+4, packet.len, 0, 4);
        int caplen = bytesToInt(packet.caplen,0,true);

        byte[] tp_mac = new byte[2];
        System.arraycopy(buf, start+4+4+4+4+4+4, tp_mac, 0, 2);
        short tp_mac_short = bytesToShort(tp_mac,0,true);

        //get packet data of size caplen
        byte[] packet_data = new byte[caplen];
        System.arraycopy(buf, start+tp_mac_short, packet_data, 0, caplen);

        //filter ip
        byte[] ether_header = new byte[14];
        byte[] ip_header = new byte[20];
        byte[] tcp_header = new byte[20];

        System.arraycopy(packet_data,0,ether_header,0,14);
        if(ether_header[12]==0x08&&ether_header[13]==0x00){
            System.arraycopy(packet_data,14,ip_header,0,20);
            int ip_header_len = (ip_header[0]&0x0f)*4;
            byte[] src_ip_byte = new byte[4];
            byte[] dst_ip_byte = new byte[4];
            System.arraycopy(ip_header,12,src_ip_byte,0,4);
            System.arraycopy(ip_header,16,dst_ip_byte,0,4);
            String sip = bytesToIp(src_ip_byte);
            String dip = bytesToIp(dst_ip_byte);
            int protocol = ip_header[9];

            //construct packetinfo
            int timestamp = bytesToInt(packet.second, 0, true);
            int length = bytesToInt(packet.len,0,true);
            packetInfo = new PacketInfo();
            packetInfo.set("IP.src", sip);
            packetInfo.set("IP.dst", dip);
            packetInfo.set("timestamp", String.valueOf(timestamp));
            packetInfo.set("length", String.valueOf(length));

            //tcp
            if(protocol == 6){
                System.arraycopy(packet_data,14 + ip_header_len,tcp_header,0,20);
                String flags = "";
                byte tmp = tcp_header[13];
                if((tmp & 0x02) != 0){
                    flags += "S";
                }
                packetInfo.set("TCP.flags", flags);
            }
        }

        //add start
        if(bytesToInt(tp_next_offset,0,true)==0){
            start += caplen + tp_mac_short;
        }else{
            start += bytesToInt(tp_next_offset,0,true);
        }

        //check to read buf
        current_packet_num++;
        if(current_packet_num == packet_num_total){
            readBuf();
        }

        if(packetInfo == null){
            return getNextPacket();
        }
        else {
            return packetInfo;
        }
    }

    @Override
    public String toString() {
        if(startTimestamp != -1){
            startTimestamp /= 1000000;
        }
        if(endTimestamp != -1){
            endTimestamp /= 1000000;
        }

        return "StenographerDriver\nPath: \"" + dirPath + "\"\nStart Timestamp: " + startTimestamp + "\nEnd Timestamp: " + endTimestamp;
    }
}
