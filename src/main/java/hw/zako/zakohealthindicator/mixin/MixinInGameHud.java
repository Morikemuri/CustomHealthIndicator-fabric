package hw.zako.zakohealthindicator.mixin;

import hw.zako.zakohealthindicator.CustomHealthIndicatorMod;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Inject(method = "render", at = @At("TAIL"))
    private void chi$render(MatrixStack ms, float dt, CallbackInfo ci) {
        CustomHealthIndicatorMod.HUD.render(ms);
    }
}
