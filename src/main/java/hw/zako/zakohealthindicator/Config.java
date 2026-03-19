package hw.zako.zakohealthindicator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;

import java.io.*;

public class Config {
    private static Config inst;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private float flyingScale = 1.0f;
    private float bigScale    = 1.0f;

    public static Config get() {
        if (inst == null) inst = new Config();
        return inst;
    }

    private Config() {
        if (cfgFile().exists()) load();
    }

    private static File cfgFile() {
        return new File(MinecraftClient.getInstance().runDirectory, "config/customhealthindicator.json");
    }

    public float getFlyingScale() { return flyingScale; }
    public float getBigScale()    { return bigScale; }

    public void setFlyingScale(float v) { flyingScale = Math.max(0.25f, Math.min(5f, v)); save(); }
    public void setBigScale(float v)    { bigScale    = Math.max(0.25f, Math.min(5f, v)); save(); }

    public void load() {
        try (FileReader r = new FileReader(cfgFile())) {
            JsonObject o = GSON.fromJson(r, JsonObject.class);
            if (o.has("flyingScale")) flyingScale = o.get("flyingScale").getAsFloat();
            if (o.has("bigScale"))    bigScale    = o.get("bigScale").getAsFloat();
        } catch (Exception ignored) {}
    }

    public void save() {
        File f = cfgFile();
        f.getParentFile().mkdirs();
        JsonObject o = new JsonObject();
        o.addProperty("flyingScale", flyingScale);
        o.addProperty("bigScale",    bigScale);
        try (FileWriter w = new FileWriter(f)) {
            GSON.toJson(o, w);
        } catch (Exception ignored) {}
    }
}
