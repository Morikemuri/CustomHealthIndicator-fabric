package hw.zako.zakohealthindicator.util;

public class ColorUtil {

    public static int hpColor(float p) {
        p = Math.max(0f, Math.min(1f, p));
        if (p <= 0.15f) {
            float pulse = 0.55f + 0.45f * (float) Math.sin(System.currentTimeMillis() * 0.008);
            return (int)(255 * pulse) << 16;
        }
        float h;
        if      (p >= 0.5f)  h = 60f  + (p - 0.5f)  / 0.5f  * 60f;
        else if (p >= 0.25f) h = 30f  + (p - 0.25f) / 0.25f * 30f;
        else                 h =         (p - 0.15f) / 0.10f * 30f;
        return hsvToRgb(h / 360f, 1f, 1f);
    }

    public static int animatedColor(float pct) {
        long t = System.currentTimeMillis();
        float h = pct * 120f + (float)((((1f - pct) * 25f + 8f)) * Math.sin(t * 0.001 * ((1f - pct) * 3f + 1f)));
        return hsvToRgb(h / 360f, 0.95f, 0.95f);
    }

    public static int getRgbColor(float pct) {
        pct = Math.max(0f, Math.min(1f, pct));
        int r, g, b = 50;
        if (pct > 0.5f) { float t = (pct - 0.5f) * 2f; r = (int)(255*(1-t)+64*t); g = 224; }
        else             { float t = pct * 2f;            r = 255; g = (int)(50*(1-t)+224*t); }
        return (r << 16) | (g << 8) | b;
    }

    public static int hsvToRgb(float h, float s, float v) {
        h -= (float) Math.floor(h);
        int   i = (int)(h * 6);
        float f = h * 6 - i, p = v*(1-s), q = v*(1-f*s), t = v*(1-(1-f)*s);
        float r, g, b;
        switch (i % 6) {
            case 0: r=v; g=t; b=p; break; case 1: r=q; g=v; b=p; break;
            case 2: r=p; g=v; b=t; break; case 3: r=p; g=q; b=v; break;
            case 4: r=t; g=p; b=v; break; default: r=v; g=p; b=q; break;
        }
        return ((int)(r*255) << 16) | ((int)(g*255) << 8) | (int)(b*255);
    }
}
