package handling.mina;

import client.MapleClient;
import tools.MapleAESOFB;
import tools.MapleCustomEncryption;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class MaplePacketDecoder extends CumulativeProtocolDecoder {

    public static final String DECODER_STATE_KEY = MaplePacketDecoder.class.getName() + ".STATE";

    public static class DecoderState {

        public int packetlength = -1;
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        final DecoderState decoderState = (DecoderState) session.getAttribute(DECODER_STATE_KEY);

        final MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);

        if (decoderState.packetlength == -1) {
            if (in.remaining() >= 4) {
                final int packetHeader = in.getInt();
                if (!client.getReceiveCrypto().checkPacket(packetHeader)) {
                    session.close(true);
                    return false;
                }
                decoderState.packetlength = MapleAESOFB.getPacketLength(packetHeader);
            } else {
                return false;
            }
        }
        if (in.remaining() >= decoderState.packetlength) {
            final byte decryptedPacket[] = new byte[decoderState.packetlength];
            in.get(decryptedPacket, 0, decoderState.packetlength);
            decoderState.packetlength = -1;

            client.getReceiveCrypto().crypt(decryptedPacket);

            out.write(decryptedPacket);
            return true;
        }
        return false;
    }
}
