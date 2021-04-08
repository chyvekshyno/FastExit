package com.tuky.diploma.structures.area;

public class Exit extends AreaObject{
    private final Zone zone;
    private final int length;
    private final Polygon polygon;

    private Exit another;

    private boolean connected;
    public Exit(Zone zone, Polygon polygon, int length) {
        this.zone = zone;
        this.polygon = polygon;
        this.length = length;
        this.connected = false;
    }

    public Zone getZone() {
        return zone;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public int getLength() {
        return length;
    }

    public Exit getAnother() {
        return another;
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

    public boolean isConnected() {
        return connected;
    }
}
