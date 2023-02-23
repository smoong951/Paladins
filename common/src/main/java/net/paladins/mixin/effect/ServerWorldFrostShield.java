package net.paladins.mixin.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.server.world.ServerWorld;
import net.paladins.effect.FrostShieldStatusEffect;
import net.paladins.effect.FrostShielded;
import net.paladins.util.SoundHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldFrostShield {
    @Inject(method = "sendEntityStatus", at = @At("HEAD"), cancellable = true)
    private void sendEntityStatus_HEAD_FrostShield(Entity entity, byte status, CallbackInfo ci) {
        if (status == EntityStatuses.BLOCK_WITH_SHIELD && entity instanceof FrostShielded shielded) {
            if (shielded.hasFrostShield()) {
                SoundHelper.playSoundEvent(entity.world, entity, FrostShieldStatusEffect.sound);
                ci.cancel();
            }
        }
    }
}
