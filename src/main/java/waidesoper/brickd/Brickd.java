package waidesoper.brickd;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import waidesoper.brickd.item.PackedSnowballItem;
import waidesoper.brickd.entity.PackedSnowballEntity;

public class Brickd implements ModInitializer {
    public static final String ModID = "brickd";

	public static final EntityType<PackedSnowballEntity> PackedSnowballEntityType = Registry.register(
		Registry.ENTITY_TYPE,
		new Identifier(ModID, "packed_snowball"),
		FabricEntityTypeBuilder.<PackedSnowballEntity>create(SpawnGroup.MISC, PackedSnowballEntity::new)
			.dimensions(EntityDimensions.fixed(0.25F, 0.25F))
			.trackRangeBlocks(4).trackedUpdateRate(10)
			.build()
	);

	public static final Item PackdSnowballItem = new PackedSnowballItem(new Item.Settings().group(ItemGroup.MISC).maxCount(16));

    @Override
    public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier(ModID, "packed_snowball"), PackdSnowballItem);
    }
}
