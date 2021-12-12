package waidesoper.brickd.client;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class EntitySpawnPacket {
    public static Packet<?> create(Entity e, Identifier packetID) {
		if (e.world.isClient)
			throw new IllegalStateException("SpawnPacketUtil.create called on the logical client!");

		PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
		byteBuf.writeVarInt(Registry.ENTITY_TYPE.getRawId(e.getType()));
		byteBuf.writeUuid(e.getUuid());
		byteBuf.writeVarInt(e.getId());

		PacketBufUtil.writeVec3d(byteBuf, e.getPos());
		byteBuf.writeFloat(e.getPitch());
		byteBuf.writeFloat(e.getYaw());
		return ServerPlayNetworking.createS2CPacket(packetID, byteBuf);
	}

	public static final class PacketBufUtil {
		/**
		 * Writes a {@link Vec3d} to a {@link PacketByteBuf}.
		 *
		 * @param byteBuf
		 *         destination buffer
		 * @param vec3d
		 *         vector
		 */
		public static void writeVec3d(PacketByteBuf byteBuf, Vec3d vec3d) {
			byteBuf.writeDouble(vec3d.x);
			byteBuf.writeDouble(vec3d.y);
			byteBuf.writeDouble(vec3d.z);
		}

		/**
		 * Reads a {@link Vec3d} from a {@link PacketByteBuf}.
		 *
		 * @param byteBuf
		 *         source buffer
		 * @return vector
		 */
		public static Vec3d readVec3d(PacketByteBuf byteBuf) {
			double x = byteBuf.readDouble();
			double y = byteBuf.readDouble();
			double z = byteBuf.readDouble();
			return new Vec3d(x, y, z);
		}
	}
}
