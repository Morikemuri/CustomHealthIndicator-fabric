package hw.zako.zakohealthindicator.client.ui;

import hw.zako.zakohealthindicator.Config;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class SettingsScreen extends Screen {

    private static final float MIN = 0.25f;
    private static final float MAX = 5.0f;
    private static final int   W   = 220;
    private static final int   H   = 20;

    private final Screen parent;

    public SettingsScreen(Screen parent) {
        super(new LiteralText("Health Indicator Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = width  / 2;
        int cy = height / 2;

        addButton(makeSlider(cx - W / 2, cy - 40, "Flying number size",
                Config.get().getFlyingScale(),
                v -> Config.get().setFlyingScale(v)));

        addButton(makeSlider(cx - W / 2, cy - 10, "Low HP number size",
                Config.get().getBigScale(),
                v -> Config.get().setBigScale(v)));

        addButton(new ButtonWidget(cx - 50, cy + 30, 100, H,
                new LiteralText("Done"),
                btn -> onClose()));
    }

    private SliderWidget makeSlider(int x, int y, String label,
                                    float init, java.util.function.Consumer<Float> onChange) {
        double norm = (init - MIN) / (MAX - MIN);
        return new SliderWidget(x, y, W, H, LiteralText.EMPTY, norm) {
            @Override
            protected void updateMessage() {
                float v = value();
                setMessage(new LiteralText(label + ": " + String.format("%.2f", v) + "x"));
            }

            @Override
            protected void applyValue() {
                onChange.accept(value());
            }

            private float value() {
                return Math.round((MIN + (float)(value * (MAX - MIN))) * 100f) / 100f;
            }
        };
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredText(matrices, textRenderer, title, width / 2, height / 2 - 65, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        client.openScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
