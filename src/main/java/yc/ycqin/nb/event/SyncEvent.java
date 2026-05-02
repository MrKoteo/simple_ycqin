package yc.ycqin.nb.event;

import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import yc.ycqin.nb.network.PacketUDLevel;
import yc.ycqin.nb.register.NetworkRegister;
import net.minecraftforge.fml.common.Optional;

public class SyncEvent {

    @Optional.Method(modid = "overlast")
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.world.isRemote) {
            if (event.world.getTotalWorldTime() % 100 == 0) { // 每5秒
                for (EntityPlayer player : event.world.playerEntities) {
                    if (player instanceof EntityPlayerMP) {
                        int udLevel = SRPSaveData.get(event.world, 59).getDeveLevel();
                        NetworkRegister.NETWORK.sendTo(new PacketUDLevel(udLevel), (EntityPlayerMP) player);
                    }
                }
            }
        }
    }
}
