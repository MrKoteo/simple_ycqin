package yc.ycqin.nb.mixins.client;

import com.dhanantry.scapeandrunparasites.util.handlers.SRPEventHandlerBus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yc.ycqin.nb.common.item.armor.AntiFogGoggles;

@Mixin(SRPEventHandlerBus.class)
public class MixinSRPEventHandlerBus {

    private static boolean hasAntiFog(EntityPlayer player) {
        if (player == null) return false;
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (helmet.isEmpty()) return false;
        if (helmet.getItem() instanceof AntiFogGoggles) return true;
        return helmet.getSubCompound("AntiFog") != null &&
                helmet.getSubCompound("AntiFog").getBoolean("Active");
    }

    @Inject(method = "playerTick", at = @At("RETURN"), remap = false)
    private void onPlayerTickReturn(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        if (hasAntiFog(event.player)) {
            SRPEventHandlerBus.fog = 0f;
        }
    }
}
