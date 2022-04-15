package mrtjp.fengine.print;

public class AsciImage {

    public final int width;
    public final int height;

    private final char[] charBuffer;

    public AsciImage(int width, int height) {
        this.width = width;
        this.height = height;

        charBuffer = new char[width * height];
    }

    public void addChar(int x, int y, char c) {
        charBuffer[y * width + x] = c;
    }

    public void addString(int x, int y, String s) {
        for (int i = 0; i < s.length(); i++) {
            charBuffer[y * width + x + i] = s.charAt(i);
        }
    }
}
