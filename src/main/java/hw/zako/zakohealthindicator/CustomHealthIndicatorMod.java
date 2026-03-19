package hw.zako.zakohealthindicator;

import hw.zako.zakohealthindicator.client.ui.HealthBarGUI;
import net.fabricmc.api.ClientModInitializer;

public class CustomHealthIndicatorMod implements ClientModInitializer {

    public static final HealthBarGUI HUD = new HealthBarGUI();

    @Override
    public void onInitializeClient() {
        // Events are handled via Mixins
    }
}
