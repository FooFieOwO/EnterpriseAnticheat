package dev.brighten.ac.utils;

import dev.brighten.ac.data.APlayer;
import dev.brighten.ac.packet.ProtocolVersion;
import dev.brighten.ac.utils.reflections.impl.MinecraftReflection;
import dev.brighten.ac.utils.reflections.types.WrappedField;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public class MovementUtils {

    private static Enchantment DEPTH;

    public static double getJumpHeight(APlayer data) {
        float baseHeight = 0.42f;

        baseHeight+= data.getInfo().groundJumpBoost.map(ef -> ef.getAmplifier() + 1)
                .orElse(0) * 0.1f;

        return baseHeight;
    }

    public static boolean isSameLocation(KLocation one, KLocation two) {
        return one.x == two.x && one.y == two.y && one.z == two.z;
    }

    public static boolean isOnLadder(APlayer data) {
        try {
            int i = MathHelper.floor_double(data.getMovement().getTo().getLoc().x);
            int j = MathHelper.floor_double(data.getMovement().getTo().getBox().yMin);
            int k = MathHelper.floor_double(data.getMovement().getTo().getLoc().z);
            Block block = BlockUtils.getBlock(new Location(data.getBukkitPlayer().getWorld(), i, j, k));

            return Materials.checkFlag(block.getType(), Materials.LADDER);
        } catch(NullPointerException e) {
            return false;
        }
    }

    private static final WrappedField checkMovement = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)
            ? MinecraftReflection.playerConnection.getFieldByName("checkMovement")
            : MinecraftReflection.playerConnection.getFieldByName(ProtocolVersion.getGameVersion()
            .isOrAbove(ProtocolVersion.V1_17) ? "y" : "teleportPos");
    public static boolean checkMovement(Object playerConnection) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            return checkMovement.get(playerConnection);
        } else return (checkMovement.get(playerConnection) == null);
    }

    public static int getDepthStriderLevel(Player player) {
        if(DEPTH == null) return 0;

        val boots = player.getInventory().getBoots();

        if(boots == null) return 0;

        return boots.getEnchantmentLevel(DEPTH);
    }

    public static double getHorizontalDistance(KLocation one, KLocation two) {
        return MathUtils.hypot(one.x - two.x, one.z - two.z);
    }

    public static float getFriction(Block block) {
        Optional<XMaterial> matched = XMaterial.matchXMaterial(block.getType().name());

        if(!matched.isPresent()) return 0.6f;
        switch(matched.get()) {
            case SLIME_BLOCK:
                return 0.8f;
            case ICE:
            case BLUE_ICE:
            case FROSTED_ICE:
            case PACKED_ICE:
                return 0.98f;
            default:
                return 0.6f;
        }
    }

    public static float getTotalHeight(float initial) {
        return getTotalHeight(ProtocolVersion.V1_8_9, initial);
    }

    public static float getTotalHeight(ProtocolVersion version, float initial) {
        float nextCalc = initial, total = initial;
        int count = 0;
        while ((nextCalc = (nextCalc - 0.08f) * 0.98f) > (version.isOrBelow(ProtocolVersion.V1_8_9) ?  0.005 : 0)) {
            total+= nextCalc;
            if(count++ > 15) {
                return total * 4;
            }
        }

        return total;
    }

    static {
        try {
            if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
                DEPTH = Enchantment.getByName("DEPTH_STRIDER");
            }

            String test = "%%__USER__%%";
        } catch(Exception e) {
            DEPTH = null;
        }
    }
}
