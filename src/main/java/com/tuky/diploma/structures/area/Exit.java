package com.tuky.diploma.structures.area;

public class Exit extends Side {

    private final Zone zone;
    private Exit another;
    private boolean connected;

    public Exit(DCoord DCoord1, DCoord DCoord2, Zone zone) {
        super(DCoord1, DCoord2);
        this.zone = zone;
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
        connect(another, true);
    }

    private void connect(Exit another, boolean startFlag) throws Exception {
        if (isConnected())              throw new Exception("This EXIT is already connected");
        if (another.isConnected())      throw new Exception("Another EXIT is already connected");
        this.another = another;
        if (startFlag)
            another.connect(this, false);
        connected = true;
    }
}
