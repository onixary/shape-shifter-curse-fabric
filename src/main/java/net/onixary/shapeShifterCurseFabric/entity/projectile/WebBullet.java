package net.onixary.shapeShifterCurseFabric.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.additional_power.WebBridgeAction;
import net.onixary.shapeShifterCurseFabric.blocks.RegCustomBlock;

import static net.onixary.shapeShifterCurseFabric.entity.RegCustomEntity.WEB_BULLET;

public class WebBullet extends ThrownItemEntity {
    public int Tier = 1;

    public static final WebBridgeAction.WebLadderConfig ladderConfigTier1 = new WebBridgeAction.WebLadderConfig(16, 20, 16, false, 0);
    public static final WebBridgeAction.WebLadderConfig ladderConfigTier2 = new WebBridgeAction.WebLadderConfig(16, 20, 16, true, 8);
    public static final WebBridgeAction.WebLadderConfig ladderConfigTier3 = new WebBridgeAction.WebLadderConfig(24, 30, 24, true, 12);

    public WebBullet(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
        this.Tier = 1;
    }

    public WebBullet(double d, double e, double f, World world, int Tier) {
        super(WEB_BULLET, d, e, f, world);
        this.Tier = Tier;
    }

    public WebBullet(LivingEntity livingEntity, int Tier) {
        super(WEB_BULLET, livingEntity, livingEntity.getWorld());
        this.Tier = Tier;
    }

    @Override
    public Item getDefaultItem() {
        return Items.WHITE_WOOL;
    }

    @Override
    public void onBlockHit(BlockHitResult blockHitResult) {
        WebBridgeAction.WebLadderConfig nowConfig = null;
        switch (Tier) {
            case 1 -> nowConfig = ladderConfigTier1;
            case 2 -> nowConfig = ladderConfigTier2;
            case 3 -> nowConfig = ladderConfigTier3;
            default -> nowConfig = ladderConfigTier1;
        }
        WebBridgeAction.BuildWebLadder(this.getWorld(), blockHitResult, nowConfig, RegCustomBlock.TEMP_WEB_BRIDGE);
        this.discard();
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult) {
        // TODO 缠丝
        super.onEntityHit(entityHitResult);
    }
}
