package yc.ycqin.nb.common.dim;

import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import yc.ycqin.nb.config.ModConfig;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class YcDimChunkGenerator implements IChunkGenerator {
    private final World world;
    private final Random random;
    private final BiomeProvider biomeProvider;
    private final NoiseGeneratorPerlin heightNoise;
    private final Map<String, IBlockState> blockStateCache = new HashMap<>();
    private final IBlockState water = Blocks.WATER.getDefaultState();

    public YcDimChunkGenerator(World world, BiomeProvider biomeProvider) {
        this.world = world;
        this.random = new Random(world.getSeed());
        this.biomeProvider = biomeProvider;
        this.heightNoise = new NoiseGeneratorPerlin(this.random, 4);
    }

    private IBlockState getBlockStateFromString(String blockMetaString) {
        if (blockMetaString == null || blockMetaString.isEmpty()) return null;
        return blockStateCache.computeIfAbsent(blockMetaString, key -> {
            String[] parts = key.split(":");
            if (parts.length >= 3) {
                Block block = Block.REGISTRY.getObject(new ResourceLocation(parts[0], parts[1]));
                if (block != null) {
                    int meta = Integer.parseInt(parts[2]);
                    return block.getStateFromMeta(meta);
                }
            }
            return null;
        });
    }

    // 生成高度图，范围 45~90
    private double[] getHeightMap(int chunkX, int chunkZ) {
        double[] heightMap = new double[16 * 16];
        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                int realX = chunkX * 16 + localX;
                int realZ = chunkZ * 16 + localZ;
                double noise = heightNoise.getValue(realX * 0.008, realZ * 0.008);
                double height = 67.5 + noise * 22.5;
                heightMap[localX * 16 + localZ] = MathHelper.clamp(height, 45, 90);
            }
        }
        return heightMap;
    }

    private void ensurePhase10(World world) {
        if (!world.isRemote) {
            SRPSaveData data = SRPSaveData.get(world, 63);
            int dim = world.provider.getDimension();
            data.setEvolutionPhase(dim, (byte)10, true, world);
        }
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        ChunkPrimer primer = new ChunkPrimer();
        double[] heightMap = getHeightMap(x, z);

        // 预获取每列的生物群系（只查一次，列坐标固定）
        Biome[] colBiomes = new Biome[16 * 16];
        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                int realX = x * 16 + localX;
                int realZ = z * 16 + localZ;
                colBiomes[localX * 16 + localZ] = biomeProvider.getBiome(new BlockPos(realX,0,realZ));
            }
        }

        // 一次性填充所有方块（直接使用寄生虫石头/泥土）
        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                int idx = localX * 16 + localZ;
                int baseHeight = (int) heightMap[idx];
                baseHeight = MathHelper.clamp(baseHeight, 0, 255);

                Biome biome = colBiomes[idx];
                IBlockState stoneState = null;
                IBlockState dirtState = null;
                if (biome instanceof BiomeParasiteBase) {
                    BiomeParasiteBase pb = (BiomeParasiteBase) biome;
                    stoneState = getBlockStateFromString(pb.getStone());
                    dirtState = getBlockStateFromString(pb.getDirt());
                }
                // 如果寄生虫石头获取失败，回退到原版石头
                if (stoneState == null) stoneState = Blocks.STONE.getDefaultState();
                if (dirtState == null) dirtState = Blocks.DIRT.getDefaultState();

                // 填充石头层
                for (int y = 0; y < baseHeight; ++y) {
                    primer.setBlockState(localX, y, localZ, stoneState);
                }
                // 顶层替换为泥土（仅最上层）
                if (baseHeight > 0) {
                    primer.setBlockState(localX, baseHeight - 1, localZ, dirtState);
                }
                // 填充水
                if (baseHeight < 63) {
                    for (int y = baseHeight; y < 63; ++y) {
                        primer.setBlockState(localX, y, localZ, water);
                    }
                }
            }
        }

        // 创建区块并设置生物群系数组
        Chunk chunk = new Chunk(world, primer, x, z);
        byte[] biomeArray = chunk.getBiomeArray();
        for (int i = 0; i < biomeArray.length; ++i) {
            int wx = (x << 4) + (i & 15);
            int wz = (z << 4) + (i >> 4);
            biomeArray[i] = (byte) Biome.getIdForBiome(biomeProvider.getBiome(new BlockPos(wx,0,wz)));
        }
        chunk.generateSkylightMap();
        ensurePhase10(world);
        return chunk;
    }

    @Override
    public void populate(int x, int z) {
        if (!ModConfig.isDimPopulateEnabled) return;
        BlockPos chunkPos = new BlockPos(x * 16, 0, z * 16);
        Random rand = new Random(world.getSeed());
        long r1 = rand.nextLong() / 2L * 2L + 1L;
        long r2 = rand.nextLong() / 2L * 2L + 1L;
        rand.setSeed((long) x * r1 + (long) z * r2 ^ world.getSeed());

        Biome biome = biomeProvider.getBiome(chunkPos.add(8, 64, 8));
        biome.decorate(world, rand, chunkPos);
    }

    // 以下为 IChunkGenerator 必需的其他方法（简单实现，不产生卡顿）
    @Override
    public boolean generateStructures(Chunk chunk, int x, int z) { return false; }
    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        return world.getBiome(pos).getSpawnableList(creatureType);
    }
    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) { return null; }
    @Override
    public void recreateStructures(Chunk chunk, int x, int z) { }
    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) { return false; }
}