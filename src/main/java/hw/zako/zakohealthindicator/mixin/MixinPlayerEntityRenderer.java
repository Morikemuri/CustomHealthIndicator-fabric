package hw.zako.zakohealthindicator.mixin;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntityRenderer.class)
public class MixinPlayerEntityRenderer {
    @Redirect(method = "renderLabelIfPresent",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/scoreboard/Scoreboard;getObjectiveForSlot(I)Lnet/minecraft/scoreboard/ScoreboardObjective;"),
            require = 0)
    private ScoreboardObjective chi$noScore(Scoreboard sb, int slot) { return null; }
}
