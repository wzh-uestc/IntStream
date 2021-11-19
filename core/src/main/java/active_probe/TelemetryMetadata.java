package active_probe;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;

public class TelemetryMetadata implements Serializable {
    byte ttl;
    int switchId;

    @Override
    public String toString() {
        return ttl + " " + switchId;
    }

    public void parseByteArray(byte[] bytes) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));

        ttl = dis.readByte();
        switchId = dis.readInt();
    }
}
