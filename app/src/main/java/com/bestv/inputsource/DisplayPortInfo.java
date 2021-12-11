package com.bestv.inputsource;

public class DisplayPortInfo {
    private int avPortType;
    private String display_port;
    private boolean multiLine;
    private String real_port;
    private int yuvPortType;

    public DisplayPortInfo(int avPortType2, int yuvPortType2, String display_port2, String real_port2, boolean multiLine2) {
        this.avPortType = avPortType2;
        this.yuvPortType = yuvPortType2;
        this.display_port = display_port2;
        this.real_port = real_port2;
        this.multiLine = multiLine2;
    }

    public DisplayPortInfo() {
        this(3, 3, "dtv-atv-av-ypbpr-hdmi1-hdmi2", "dtv-atv-av-ypbpr-hdmi1-hdmi2", false);
    }

    public String getDisplayPort() {
        return this.display_port;
    }

    public void setDisplayPort(String display_port2) {
        this.display_port = display_port2;
    }

    public String getRealPort() {
        return this.real_port;
    }

    public void setRealPort(String real_port2) {
        this.real_port = real_port2;
    }

    public int getAvPortType() {
        return this.avPortType;
    }

    public void setAvPortType(int avPortType2) {
        this.avPortType = avPortType2;
    }

    public int getYuvPortType() {
        return this.yuvPortType;
    }

    public void setYuvPortType(int yuvPortType2) {
        this.yuvPortType = yuvPortType2;
    }

    public boolean getMultiLine() {
        return this.multiLine;
    }

    public void setMultiLine(boolean multiLine2) {
        this.multiLine = multiLine2;
    }
}
