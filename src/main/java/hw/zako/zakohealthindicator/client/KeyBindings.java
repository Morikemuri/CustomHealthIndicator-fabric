package hw.zako.zakohealthindicator.client;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyBinding OPEN_SETTINGS = new KeyBinding(
            "Open Health Indicator Settings",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "Health Indicator"
    );
}
