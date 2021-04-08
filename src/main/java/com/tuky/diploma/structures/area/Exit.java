package com.tuky.diploma.structures.area;

import java.util.HashMap;

public class Exit extends Polygon{

    private static int IND = 0;
    private static HashMap<Integer, Exit> exits = new HashMap<>();

    private final Zone zone;
    private Exit another;
    private boolean connected;

    private final int _ind;

    public Exit(Coord coord1, Coord coord2, Zone zone) {
        super(coord1, coord2);
        this.zone = zone;
        this._ind = IND++;
    }

    public int getInd() {
        return _ind;
    }

    public Zone getZone() {
        return zone;
    }

    public Exit getAnother() {
        return another;
    }

    public boolean isConnected() {
        return connected;
    }

    public void disconnect() {
        another = null;
        connected = false;
    }

    public void connect(Exit another) throws Exception {
        if (isConnected())              throw new Exception("This EXIT is already connected");
        if (another.isConnected())      throw new Exception("Another EXIT is already connected");
        this.another = another;
        another.connect(this);
        connected = true;
    }
}
