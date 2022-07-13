package mrfast.skyblockfeatures.utils;

import java.awt.*;

public class AColor extends Color {
    private boolean chroma;
    private float chromaSpeed;

    public AColor(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public AColor(int rgba, boolean hasalpha) {
        super(rgba, hasalpha);
    }

    public AColor(AColor clone) {
        super(clone.getRGB(), true);
    }

    public AColor multiplyAlpha(double multiplier) {
        AColor aColor = new AColor(getRed(), getGreen(), getBlue(), (int) (getAlpha() * multiplier));
        aColor.chroma = this.chroma;
        aColor.chromaSpeed = this.chromaSpeed;
        return aColor;
    }

    @Override
    public String toString() {
        return "AColor{" +
                ", r="+getRed()+
                ", g="+getGreen()+
                ", b="+getBlue()+
                ", a="+getAlpha()+
                '}';
    }
}
