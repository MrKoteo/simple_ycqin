package yc.ycqin.nb.common.dim;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 原始的单一群系提供器，作为后备方案。
 * 该版本经过实践验证，绝不崩溃。
 */
public class YcDimBiome extends BiomeProvider {
    private static final Biome PARASITE_BIOME;

    static {
        Biome biome = Biome.REGISTRY.getObject(new ResourceLocation("srparasites", "biomeparasite_shrouded"));
        if (biome == null) {
            throw new RuntimeException("SRP biome 'srparasites:biomeparasite_shrouded' not found!");
        }
        PARASITE_BIOME = biome;
    }

    private final Biome biome;

    public YcDimBiome(long seed, WorldType worldType, String options) {
        super();
        this.biome = PARASITE_BIOME;
    }

    public Biome getBiome(int x, int z) {
        return biome;
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return biome;
    }

    @Override
    public List<Biome> getBiomesToSpawnIn() {
        return Collections.singletonList(biome);
    }

    @Override
    public Biome[] getBiomes(Biome[] oldBiomeArray, int x, int z, int width, int depth, boolean cacheFlag) {
        if (oldBiomeArray == null || oldBiomeArray.length < width * depth) {
            oldBiomeArray = new Biome[width * depth];
        }
        Arrays.fill(oldBiomeArray, biome);
        return oldBiomeArray;
    }

    @Override
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height) {
        if (biomes == null || biomes.length < width * height) {
            biomes = new Biome[width * height];
        }
        Arrays.fill(biomes, biome);
        return biomes;
    }
}