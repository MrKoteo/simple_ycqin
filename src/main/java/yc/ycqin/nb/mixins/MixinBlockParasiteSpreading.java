package yc.ycqin.nb.mixins;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yc.ycqin.nb.common.entity.tileentity.TileEntityParasiteCore;

import java.util.Random;

@Mixin(BlockParasiteSpreading.class)
public abstract class MixinBlockParasiteSpreading {

    @Inject(
            method = "SpreadBiome",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void onSpreadBiome(World worldIn, BlockPos pos, int age, int type, CallbackInfo ci) {
        if (TileEntityParasiteCore.isPositionProtected(worldIn,pos)) {
            ci.cancel(); // 阻止群系感染
            // 可额外处理：将该位置的生物群系强制设为安全群系
            // 参考 BlockParasiteSpreading.positionToParasiteBiome 的反向操作
        }
    }

    @Inject(
            method = "spreadBiomeBlockStain",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void onSpreadBiomeBlockStain(World worldIn, BlockPos pos, Random rand, CallbackInfo ci){
        if (worldIn.provider.getDimension() == 78){
            ci.cancel();
        }
    }

}