package tools.data.output;

import java.io.ByteArrayOutputStream;
import tools.HexTool;

public class MaplePacketLittleEndianWriter extends GenericLittleEndianWriter {

    private final ByteArrayOutputStream baos;

    public MaplePacketLittleEndianWriter() {
        this(32);
    }

    public MaplePacketLittleEndianWriter(final int size) {
        this.baos = new ByteArrayOutputStream(size);
        setByteOutputStream(new BAOSByteOutputStream(baos));
    }

    public final byte[] getPacket() {
        //byte[] packet = new ByteArrayMaplePacket(baos.toByteArray());
        //System.out.println("Packet to be sent:\n" +packet.toString());
        return baos.toByteArray();
    }

    @Override
    public final String toString() {
        return HexTool.toString(baos.toByteArray());
    }
}
