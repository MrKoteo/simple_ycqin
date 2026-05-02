package yc.ycqin.nb.register;

import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import yc.ycqin.nb.common.potion.PotionStun;

@Mod.EventBusSubscriber(modid = "ycqin")
public class PotionsRegister {
    public static final Potion STUN;
    public static PotionType STUN_TYPE;

    @SubscribeEvent
    public static void onEventE(RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(STUN);
    }

    @SubscribeEvent
    public static void onEventP(RegistryEvent.Register<PotionType> event){
        event.getRegistry().register(STUN_TYPE);
    }

    static {
        STUN = new PotionStun();
        STUN_TYPE = new PotionType("ycqin:stun", new PotionEffect(STUN, 200, 0));
        STUN_TYPE.setRegistryName("ycqin:stun");
    }
}