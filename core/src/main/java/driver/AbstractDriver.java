package driver;

import primitives.PacketInfo;

import java.io.IOException;

public interface AbstractDriver {
    PacketInfo getNextPacket() throws Exception;
}
