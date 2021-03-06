package us.myles.ViaVersion.api.type.types.minecraft;

import com.github.steveice10.opennbt.NBTIO;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import us.myles.ViaVersion.api.type.Type;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;

public class NBTType extends Type<CompoundTag> {
    public NBTType() {
        super(CompoundTag.class);
    }

    @Override
    public CompoundTag read(ByteBuf buffer) throws Exception {
        Preconditions.checkArgument(buffer.readableBytes() <= 2097152, "Cannot read NBT (got %s bytes)", buffer.readableBytes());

        int readerIndex = buffer.readerIndex();
        byte b = buffer.readByte();
        if (b == 0) {
            return null;
        } else {
            buffer.readerIndex(readerIndex);
            ByteBufInputStream bytebufStream = new ByteBufInputStream(buffer);
            try (DataInputStream dataInputStream = new DataInputStream(bytebufStream)) {
                return (CompoundTag) NBTIO.readTag((DataInput) dataInputStream);
            }
        }
    }

    @Override
    public void write(ByteBuf buffer, CompoundTag object) throws Exception {
        if (object == null) {
            buffer.writeByte(0);
        } else {
            ByteBufOutputStream bytebufStream = new ByteBufOutputStream(buffer);
            DataOutputStream dataOutputStream = new DataOutputStream(bytebufStream);

            NBTIO.writeTag((DataOutput) dataOutputStream, object);

            dataOutputStream.close();
        }
    }
}
