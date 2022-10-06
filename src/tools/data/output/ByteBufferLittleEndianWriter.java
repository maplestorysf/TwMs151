package tools.data.output;

import org.apache.mina.core.buffer.IoBuffer;

public class ByteBufferLittleEndianWriter extends GenericLittleEndianWriter {

    private IoBuffer bb;

    public ByteBufferLittleEndianWriter() {
        this(50, true);
    }

    public ByteBufferLittleEndianWriter(final int size) {
        this(size, false);
    }

    public ByteBufferLittleEndianWriter(final int initialSize, final boolean autoExpand) {
        bb = IoBuffer.allocate(initialSize);
        bb.setAutoExpand(autoExpand);
        setByteOutputStream(new ByteBufferOutputstream(bb));
    }

    public IoBuffer getFlippedBB() {
        return bb.flip();
    }

    public IoBuffer getByteBuffer() {
        return bb;
    }
}
