package driver.pcap_driver;

import driver.AbstractDriver;
import primitives.PacketInfo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class PcapDriver implements AbstractDriver {

    private BufferedInputStream pcapBufferedReader;

    public PcapDriver(String pcapFile) throws IOException {
        pcapBufferedReader = new BufferedInputStream(new FileInputStream(pcapFile), 65536);

        byte[] pcap_header = new byte[24];
        pcapBufferedReader.read(pcap_header);
    }


    public PacketInfo getNextPacket() throws IOException{
        byte[] buff = new byte[16];
        int len = 0;
        PacketHeader packet_header = new PacketHeader();
        PacketInfo packetInfo = null;

        boolean isheader = true;
        while((len = pcapBufferedReader.read(buff)) != -1){
            if(isheader == true){
                packet_header.set_header(buff);
                int caplen = PacketParse.bytesToInt(packet_header.caplen,0,true);
                buff = new byte[caplen];
                isheader = false;
            }else{
                byte[] ether_header = new byte[14];
                byte[] ip_header = new byte[20];
                System.arraycopy(buff,0,ether_header,0,14);
                if(ether_header[12]==0x08&&ether_header[13]==0x00){
                    System.arraycopy(buff,14,ip_header,0,20);
                    byte[] src_ip_byte = new byte[4];
                    byte[] dst_ip_byte = new byte[4];
                    System.arraycopy(ip_header,12,src_ip_byte,0,4);
                    System.arraycopy(ip_header,16,dst_ip_byte,0,4);
                    String sip = PacketParse.bytesToIp(src_ip_byte);
                    String dip = PacketParse.bytesToIp(dst_ip_byte);
                    int protocol = ip_header[9];

                    int timestamp = PacketParse.bytesToInt(packet_header.second,0,true);
                    int length = PacketParse.bytesToInt(packet_header.len, 0, true);

                    packetInfo = new PacketInfo();
                    packetInfo.set("IP.src", sip);
                    packetInfo.set("IP.dst", dip);
                    packetInfo.set("timestamp", String.valueOf(timestamp));
                    packetInfo.set("length", String.valueOf(length));


                    return packetInfo;
                }

                isheader = true;
                buff = new byte[16];
            }
        }

        return packetInfo;
    }

}
