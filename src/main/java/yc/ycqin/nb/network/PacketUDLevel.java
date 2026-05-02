package yc.ycqin.nb.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yc.ycqin.nb.client.ClientUDData;

public class PacketUDLevel implements IMessage {
    private int udLevel;

    public PacketUDLevel() {}

    public PacketUDLevel(int level) {
        this.udLevel = level;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        udLevel = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(udLevel);
    }

    public static class Handler implements IMessageHandler<PacketUDLevel, IMessage> {
        @Override
        public IMessage onMessage(PacketUDLevel message, MessageContext ctx) {
            // 在客户端线程更新缓存
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() -> {
                ClientUDData.udLevel = message.udLevel;
            });
            return null;
        }
    }
}