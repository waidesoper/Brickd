package waidesoper.brickd.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
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
import waidesoper.brickd.packet.EntitySpawnPacket;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class BrickdClient implements ClientModInitializer {
    public static final Identifier PacketID = new Identifier(Brickd.ModID, "spawn_packet");
    @Override
    public void onInitializeClient() {
		EntityRendererRegistry.register(Brickd.PackedSnowballEntityType, (context) ->
				 new FlyingItemEntityRenderer(context));
        ClientPlayNetworking.registerGlobalReceiver(PacketID,(client, handler, buf, responseSender) -> receiveEntityPacket(client, handler, buf, responseSender));
    }

    public void receiveEntityPacket(MinecraftClient minecraftClient, ClientPlayNetworkHandler handler, PacketByteBuf byteBuf, PacketSender responseSender) {
        EntityType<?> et = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
        UUID uuid = byteBuf.readUuid();
        int entityId = byteBuf.readVarInt();
        Vec3d pos = EntitySpawnPacket.PacketBufUtil.readVec3d(byteBuf);
        float pitch = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
        float yaw = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
        minecraftClient.execute(() -> {
            if (MinecraftClient.getInstance().world == null)
                throw new IllegalStateException("Tried to spawn entity in a null world!");
            Entity e = et.create(MinecraftClient.getInstance().world);
            if (e == null)
                throw new IllegalStateException("Failed to create instance of entity \"" + Registry.ENTITY_TYPE.getId(et) + "\"!");
            e.updateTrackedPosition(pos);
            e.setPos(pos.x, pos.y, pos.z);
            e.setPitch(pitch);
            e.setYaw(yaw);
            e.setId(entityId);
            e.setUuid(uuid);
            MinecraftClient.getInstance().world.addEntity(entityId, e);
        });
    }
}
