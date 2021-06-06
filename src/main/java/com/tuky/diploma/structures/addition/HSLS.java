package com.tuky.diploma.structures.addition;

public class HSLS implements Comparable<HSLS>{
    private final int y;
    private final int xL;
    private final int xR;

    private final int flag;

    public HSLS(int y, int xL, int xR, int flag) {
        this.y = y;
        this.xL = xL;
        this.xR = xR;
        this.flag = flag;
    }

    public int y() {
        return y;
    }

    public int xL() {
        return xL;
    }

    public int xR() {
        return xR;
    }

    @Override
    public int compareTo(HSLS hsls) {
        return Integer.compare(this.xR - this.xL, hsls.xR() - hsls.xL());
    }

    public int width() {
        return xR - xL;
    }

    public boolean isLower(HSLS another) {
        return this.compareTo(another) < 0;
    }

    public boolean isBigger(HSLS another) {
        return this.compareTo(another) > 0;
    }

    public boolean isEqual(HSLS another) {
        return this.compareTo(another) == 0;
    }

    public int getFlag() {
        return flag;
    }
}
