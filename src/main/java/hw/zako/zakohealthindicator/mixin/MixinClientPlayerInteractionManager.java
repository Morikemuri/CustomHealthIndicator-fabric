package hw.zako.zakohealthindicator.mixin;

import hw.zako.zakohealthindicator.CustomHealthIndicatorMod;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void chi$attack(PlayerEntity p, Entity t, CallbackInfo ci) {
        if (t instanceof LivingEntity) CustomHealthIndicatorMod.HUD.onAttack((LivingEntity) t);
    }
}
