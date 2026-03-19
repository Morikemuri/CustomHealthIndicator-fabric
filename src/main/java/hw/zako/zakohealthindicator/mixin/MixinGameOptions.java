package hw.zako.zakohealthindicator.mixin;

import hw.zako.zakohealthindicator.client.KeyBindings;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(GameOptions.class)
public class MixinGameOptions {

    @Shadow
    public KeyBinding[] keysAll;

    @Inject(method = "<init>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/option/GameOptions;load()V"))
    private void chi$injectKey(CallbackInfo ci) {
        // Register our custom category so Controls screen doesn't crash when sorting
        Map<String, Integer> map = KeyBindingAccessor.getCategoryOrderMap();
        if (!map.containsKey("Health Indicator")) {
            int max = map.values().stream().mapToInt(Integer::intValue).max().orElse(0);
            map.put("Health Indicator", max + 1);
        }

        KeyBinding[] arr = new KeyBinding[this.keysAll.length + 1];
        System.arraycopy(this.keysAll, 0, arr, 0, this.keysAll.length);
        arr[this.keysAll.length] = KeyBindings.OPEN_SETTINGS;
        this.keysAll = arr;
    }
}
