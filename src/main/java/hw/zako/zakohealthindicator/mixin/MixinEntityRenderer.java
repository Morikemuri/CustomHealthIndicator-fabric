package hw.zako.zakohealthindicator.mixin;

import hw.zako.zakohealthindicator.util.ColorUtil;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Shadow
    protected abstract void renderLabelIfPresent(Entity entity, Text text,
            MatrixStack ms, VertexConsumerProvider vcp, int light);

    // Intercept the renderLabelIfPresent call from render() BEFORE the background rect is drawn.
    // This way both the background and the text use the correct (wider) modified width.
    @Redirect(
        method = "render",
        at = @At(value = "INVOKE",
                 target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderLabelIfPresent(" +
                          "Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;" +
                          "Lnet/minecraft/client/util/math/MatrixStack;" +
                          "Lnet/minecraft/client/render/VertexConsumerProvider;I)V"),
        require = 0
    )
    private void chi$label(EntityRenderer<?> self, Entity entity, Text text,
                            MatrixStack ms, VertexConsumerProvider vcp, int light) {
        // Suppress integer-only nametags on non-players (server HP armor stands)
        if (!(entity instanceof PlayerEntity)) {
            try {
                int n = Integer.parseInt(text.getString().trim());
                if (n >= 0 && n <= 40) return;
            } catch (NumberFormatException ignored) {}
            renderLabelIfPresent(entity, text, ms, vcp, light);
            return;
        }

        // Build modified text: "Name HP❤" with HP colored, heart from custom font
        float hp  = ((LivingEntity) entity).getHealth();
        float max = ((LivingEntity) entity).getMaxHealth();
        int   rgb = ColorUtil.hpColor(max > 0f ? hp / max : 1f);
        TextColor color = TextColor.fromRgb(rgb);
        Style numStyle  = Style.EMPTY.withColor(color);
        Style heartStyle = Style.EMPTY.withColor(color)
                .withFont(new Identifier("customhealthindicator", "icons"));

        MutableText modified = new LiteralText("").append(text)
                .append(new LiteralText(" " + String.format("%.1f", hp)).setStyle(numStyle))
                .append(new LiteralText("\uE001").setStyle(heartStyle));

        renderLabelIfPresent(entity, modified, ms, vcp, light);
    }
}
