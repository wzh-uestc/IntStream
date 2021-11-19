package active_probe;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPProbe {
    private int srcPort;
    private String srcIp;

    private DatagramSocket socket;

    public UDPProbe(String srcIp, int srcPort) throws Exception{
        this.srcIp = srcIp;
        this.srcPort = srcPort;
        this.socket = new DatagramSocket(srcPort, InetAddress.getByName(srcIp));
    }

    public void sendProbe(String dip, int dPort, byte[] data) throws Exception{
        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(dip), dPort);
        socket.send(packet);
    }

    public TelemetryMetadata recvReport() throws Exception{
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        System.out.println("receiving on " + socket.getLocalAddress() + "\t" + socket.getLocalPort());
        socket.receive(packet);

        //buf -> metadata
        TelemetryMetadata metadata = new TelemetryMetadata();
        metadata.parseByteArray(packet.getData());
        System.out.println(metadata);
        return metadata;
    }

    public static void main(String[] args) throws Exception {
        UDPProbe probe = new UDPProbe("10.0.1.101", 5000);

        probe.sendProbe("10.0.2.101", 1234, new byte[5]);
        probe.recvReport();
        probe.recvReport();
    }
}
