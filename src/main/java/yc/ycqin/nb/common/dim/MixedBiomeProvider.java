package yc.ycqin.nb.common.dim;

import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 混合寄生虫生物群系提供器。
 * 如果任何群系获取失败，自动降级为单一群系后备（YcDimBiome行为）。
 */
public class MixedBiomeProvider extends BiomeProvider {

    // 候选群系列表（仅使用SRP已注册的群系）
    private static final Biome[] CANDIDATES = new Biome[2];
    // 后备群系（单一寄生虫群系）
    private static final Biome FALLBACK_BIOME;

    static {
        // 初始化候选群系
        Biome shrouded = Biome.REGISTRY.getObject(new ResourceLocation("srparasites", "biomeparasite_shrouded"));
        Biome harlequin = Biome.REGISTRY.getObject(new ResourceLocation("srparasites", "biomeparasite_harlequin"));
        CANDIDATES[0] = shrouded != null ? shrouded : Biomes.PLAINS;
        CANDIDATES[1] = harlequin != null ? harlequin : Biomes.PLAINS;

        // 设置后备群系（与原始YcDimBiome相同）
        FALLBACK_BIOME = shrouded != null ? shrouded : Biomes.PLAINS;
    }

    private final NoiseGeneratorPerlin noiseGen;
    private final boolean useMixed;  // 混合模式开关（可通过配置控制）

    public MixedBiomeProvider(long seed) {
        this(seed, true); // 默认启用混合模式
    }

    public MixedBiomeProvider(long seed, boolean enableMixed) {
        super();
        this.useMixed = enableMixed;
        this.noiseGen = new NoiseGeneratorPerlin(new Random(seed), 1);
    }

    public Biome getBiome(int x, int z) {
        if (!useMixed) {
            return FALLBACK_BIOME; // 直接返回后备
        }
        try {
            double noise = noiseGen.getValue(x * 0.008, z * 0.008);
            int index = (int) ((noise + 1.0) * 0.5 * CANDIDATES.length);
            index = Math.min(CANDIDATES.length - 1, Math.max(0, index));
            Biome b = CANDIDATES[index];
            return b != null ? b : FALLBACK_BIOME;
        } catch (Exception e) {
            // 任何异常都降级为后备
            return FALLBACK_BIOME;
        }
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return getBiome(pos.getX(), pos.getZ());
    }

    @Override
    public List<Biome> getBiomesToSpawnIn() {
        return useMixed ? Arrays.asList(CANDIDATES) : Arrays.asList(FALLBACK_BIOME);
    }

    @Override
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height) {
        if (biomes == null || biomes.length < width * height) {
            biomes = new Biome[width * height];
        }
        for (int i = 0; i < width * height; i++) {
            int wx = x + (i % width);
            int wz = z + (i / width);
            biomes[i] = getBiome(wx, wz);
        }
        return biomes;
    }

    @Override
    public Biome[] getBiomes(Biome[] oldBiomeArray, int x, int z, int width, int depth, boolean cacheFlag) {
        // 直接复用 getBiomesForGeneration 的实现
        return getBiomesForGeneration(oldBiomeArray, x, z, width, depth);
    }
}