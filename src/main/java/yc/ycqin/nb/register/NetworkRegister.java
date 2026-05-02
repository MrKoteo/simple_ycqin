package yc.ycqin.nb.register;

import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import yc.ycqin.nb.network.PacketUDLevel;

public class NetworkRegister {
    public static SimpleNetworkWrapper NETWORK;
    public static int id = 0;
    public NetworkRegister(){
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("ycqin");
        NETWORK.registerMessage(PacketUDLevel.Handler.class, PacketUDLevel.class, id++, Side.CLIENT);
    }
}
