package me.bumblebeee_.magic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class Util {

    public void throwb(Material m, Location l) {
        double x;
        double y = 0.6;
        double z;
        BlockFace f = yawToFace(l.getYaw());

        l = right(f, right(f, right(f, l)));
        x = (float) -1 + (float) (Math.random() * ((1 - -1) + 1));
        z = (float) -0.3 + (float)(Math.random() * ((0.3 - -0.3) + 1));

        final FallingBlock block = l.getWorld().spawnFallingBlock(l, new MaterialData(m));
        block.setVelocity(new Vector(x, y, z));
        block.setDropItem(false);
        block.setHurtEntities(false);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Magic.getInstance(), new Runnable() {
            @Override
            public void run() {
                block.remove();
            }
        }, 10);


        x = (float) -1 + (float) (Math.random() * ((1 - -1) + 1));
        z = (float) -0.3 + (float)(Math.random() * ((0.3 - -0.3) + 1));
        l = left(f, left(f, left(f, l)));

        final FallingBlock block2 = l.getWorld().spawnFallingBlock(l, new MaterialData(m));
        block2.setMetadata("morph", new FixedMetadataValue(Magic.getInstance(), "true"));
        block2.setVelocity(new Vector(x, y, z));
        block2.setDropItem(false);
        block.setHurtEntities(false);
    }

    public Location left(BlockFace f, Location loc) {
        Location l = null;
        switch (f) {
            case NORTH:
                l = loc.add(-1,0,0);
                break;
            case SOUTH:
                l = loc.add(1,0,0);
                break;
            case EAST:
                l = loc.add(0,0,-1);
                break;
            case WEST:
                l = loc.add(0,0,1);
                break;
        }
        return l;
    }
    public Location right(BlockFace f, Location loc) {
        Location l = null;
        switch (f) {
            case NORTH:
                l = loc.add(1,0,0);
                break;
            case SOUTH:
                l = loc.add(-1,0,0);
                break;
            case EAST:
                l = loc.add(0,0,1);
                break;
            case WEST:
                l = loc.add(0,0,-1);
                break;
        }
        return l;
    }

    public BlockFace yawToFace(float yaw) {
        return axis[Math.round(yaw / 90f) & 0x3].getOppositeFace();
    }
    private static final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

}
