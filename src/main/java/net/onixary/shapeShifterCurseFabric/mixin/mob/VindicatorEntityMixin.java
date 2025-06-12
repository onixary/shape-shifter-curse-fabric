package net.onixary.shapeShifterCurseFabric.mixin.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.team.MobTeamManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VindicatorEntity.class)
public abstract class VindicatorEntityMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void addToSorceryTeam(EntityType<? extends HostileEntity> entityType, World world, CallbackInfo ci) {
        if (!world.isClient) {
            Entity entity = (Entity)(Object)this;
            Scoreboard scoreboard = world.getScoreboard();

            Team sorceryTeam = scoreboard.getTeam(MobTeamManager.SORCERY_TEAM_NAME);
            if (sorceryTeam != null) {
                scoreboard.addPlayerToTeam(entity.getEntityName(), sorceryTeam);
            }
        }
    }
}
