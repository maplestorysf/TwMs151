package tools.data.output;

import org.apache.mina.core.buffer.IoBuffer;

public class ByteBufferOutputstream implements ByteOutputStream {

    private IoBuffer bb;

    public ByteBufferOutputstream(final IoBuffer bb) {
        super();
        this.bb = bb;
    }

    @Override
    public void writeByte(final byte b) {
        bb.put(b);
    }
}
