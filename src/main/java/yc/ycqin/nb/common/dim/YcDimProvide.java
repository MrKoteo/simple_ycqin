package yc.ycqin.nb.common.dim;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.IChunkGenerator;
import yc.ycqin.nb.register.DimRegister;

public class YcDimProvide extends WorldProvider {

    @Override
    public DimensionType getDimensionType() {
        return DimRegister.ycdim;
    }

    @Override
    public String getSaveFolder() {
        return "ycdim";
    }

    @Override
    public void init() {
        this.biomeProvider = new MixedBiomeProvider(world.getSeed());
        this.hasSkyLight = false; // 可选，无日光
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new YcDimChunkGenerator(world, biomeProvider);
    }
}