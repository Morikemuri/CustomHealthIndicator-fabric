package hw.zako.zakohealthindicator.mixin;

import hw.zako.zakohealthindicator.client.KeyBindings;
import hw.zako.zakohealthindicator.client.ui.SettingsScreen;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Shadow private FontManager fontManager;

    @Unique private boolean chi$injected = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void chi$tick(CallbackInfo ci) {
        MinecraftClient mc = (MinecraftClient)(Object)this;

        if (mc.currentScreen == null && KeyBindings.OPEN_SETTINGS.wasPressed())
            mc.openScreen(new SettingsScreen(null));

        if (!chi$injected) chi$ensureHeartFont(mc);
    }

    @Unique
    private void chi$ensureHeartFont(MinecraftClient mc) {
        try {
            FontManagerAccessor acc = (FontManagerAccessor)(Object)fontManager;
            Map<Identifier, FontStorage> storages = acc.getFontStorages();

            Identifier defaultId = new Identifier("minecraft", "default");
            Identifier iconId    = new Identifier("customhealthindicator", "icons");

            if (!storages.containsKey(defaultId)) return;

            Font font = chi$loadFont();
            if (font == null) { System.out.println("[CHI] loadFont returned null"); return; }

            FontStorage storage = new FontStorage(acc.getTextureManager(), iconId);

            Method setFonts = null;
            for (Method m : FontStorage.class.getDeclaredMethods()) {
                if (m.getParameterCount() == 1 && List.class.isAssignableFrom(m.getParameterTypes()[0])) {
                    setFonts = m; break;
                }
            }
            if (setFonts == null) { System.out.println("[CHI] setFonts not found"); return; }
            setFonts.setAccessible(true);
            setFonts.invoke(storage, Collections.singletonList(font));

            storages.put(iconId, storage);
            chi$injected = true;
            System.out.println("[CHI] heart font injected OK");
        } catch (Exception e) {
            System.out.println("[CHI] ensureHeartFont exc: " + e);
            e.printStackTrace();
        }
    }

    @Unique
    private Font chi$loadFont() {
        try {
            InputStream is = MixinMinecraftClient.class.getClassLoader()
                    .getResourceAsStream("assets/customhealthindicator/textures/font/heart.png");
            if (is == null) { System.out.println("[CHI] heart.png not found"); return null; }

            NativeImage img = NativeImage.read(is);
            is.close();

            int W = img.getWidth(), H = img.getHeight();
            System.out.println("[CHI] heart.png loaded: " + W + "x" + H);

            // oversample = 9/16 → renders at 9x9 screen pixels
            // getAscent() = charHeight * oversample = 16 * 0.5625 = 9
            // y0 = -getAscent() / oversample = -9 / 0.5625 = -16
            // Correction applied in MixinEntityRenderer: HEART_Y_FIX = charHeight - 7 = 16 - 7 = 9
            float oversample = 9.0f / H;

            Class<?> glyphClass = null;
            for (Class<?> c : BitmapFont.class.getDeclaredClasses()) {
                if (!c.equals(BitmapFont.Loader.class)) { glyphClass = c; break; }
            }
            if (glyphClass == null) { System.out.println("[CHI] no glyph class"); return null; }

            Constructor<?> glyphCtor = null;
            for (Constructor<?> c : glyphClass.getDeclaredConstructors()) {
                Class<?>[] pt = c.getParameterTypes();
                if (pt.length == 8 && pt[0] == float.class) { glyphCtor = c; break; }
            }
            if (glyphCtor == null) { System.out.println("[CHI] glyph ctor not found"); return null; }

            // ascent=12 → getAscent()=12*0.5625=6.75≈7, aligns heart with text in combined draw
            glyphCtor.setAccessible(true);
            int ascent = Math.round(7.0f / oversample);
            Object glyph = glyphCtor.newInstance(oversample, img, 0, 0, W, H, W, ascent);

            Int2ObjectOpenHashMap<Object> glyphMap = new Int2ObjectOpenHashMap<>();
            glyphMap.put(0xE001, glyph);

            for (Constructor<?> c : BitmapFont.class.getDeclaredConstructors()) {
                if (c.getParameterTypes().length == 2) {
                    c.setAccessible(true);
                    Font f = (Font) c.newInstance(img, glyphMap);
                    System.out.println("[CHI] BitmapFont created!");
                    return f;
                }
            }
            System.out.println("[CHI] BitmapFont 2-param ctor not found");
        } catch (Exception e) {
            System.out.println("[CHI] loadFont exc: " + e);
            e.printStackTrace();
        }
        return null;
    }
}
