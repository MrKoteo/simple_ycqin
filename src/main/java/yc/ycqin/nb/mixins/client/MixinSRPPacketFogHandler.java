package yc.ycqin.nb.mixins.client;

import com.dhanantry.scapeandrunparasites.network.SRPPacketFog;
import com.dhanantry.scapeandrunparasites.util.handlers.SRPEventHandlerBus;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yc.ycqin.nb.common.item.armor.AntiFogGoggles;

@Mixin(SRPPacketFog.Handler.class)
public abstract class MixinSRPPacketFogHandler {

    private static boolean hasAntiFog(EntityPlayer player) {
        if (player == null) return false;
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (helmet.isEmpty()) return false;
        if (helmet.getItem() instanceof AntiFogGoggles) return true;
        return helmet.getSubCompound("AntiFog") != null &&
                helmet.getSubCompound("AntiFog").getBoolean("Active");
    }

    @Inject(method = "handle", at = @At("HEAD"), remap = false, cancellable = true)
    private void onHandle(SRPPacketFog message, MessageContext ctx, CallbackInfo ci) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null && hasAntiFog(player)) {
            ci.cancel();
        }
    }
}
