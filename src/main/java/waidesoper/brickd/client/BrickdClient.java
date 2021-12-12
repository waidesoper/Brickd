package waidesoper.brickd.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import waidesoper.brickd.Brickd;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class BrickdClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
		EntityRendererRegistry.register(Brickd.PackedSnowballEntityType, FlyingItemEntityRenderer::new);
        ClientPlayNetworking.registerGlobalReceiver(Brickd.PacketID, this::receiveEntityPacket);
    }

    public void receiveEntityPacket(MinecraftClient minecraftClient, ClientPlayNetworkHandler handler, PacketByteBuf byteBuf, PacketSender responseSender) {
        EntityType<?> et = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
        UUID uuid = byteBuf.readUuid();
        int entityId = byteBuf.readVarInt();
        Vec3d pos = EntitySpawnPacket.PacketBufUtil.readVec3d(byteBuf);
        float pitch = byteBuf.readFloat();  // EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
        float yaw = byteBuf.readFloat();    //EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);

        minecraftClient.execute(() -> {
            if (minecraftClient.world == null)
                throw new IllegalStateException("Tried to spawn entity in a null world!");

            Entity e = et.create(minecraftClient.world);
            if (e == null)
                throw new IllegalStateException("Failed to create instance of entity \"" + Registry.ENTITY_TYPE.getId(et) + "\"!");

            e.updateTrackedPosition(pos);
            e.setPos(pos.x, pos.y, pos.z);
            e.setPitch(pitch);
            e.setYaw(yaw);
            e.setId(entityId);
            e.setUuid(uuid);

            minecraftClient.world.addEntity(entityId, e);
        });
    }
}
