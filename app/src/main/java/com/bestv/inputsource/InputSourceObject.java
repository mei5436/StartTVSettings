package com.bestv.inputsource;

public class InputSourceObject {
    private String icon;
    private int id;
    private int inputSrc;
    private boolean singalLock;
    private String title;

    public InputSourceObject(int inputSrc2, String icon2, String title2, int id2) {
        this(inputSrc2, icon2, title2, id2, false);
    }

    public InputSourceObject(int inputSrc2, String icon2, String title2, int id2, boolean singalLock2) {
        this.inputSrc = inputSrc2;
        this.icon = icon2;
        this.title = title2;
        this.singalLock = singalLock2;
        this.id = id2;
    }

    public int getInputSrc() {
        return this.inputSrc;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getTitle() {
        return this.title;
    }

    public int getId() {
        return this.id;
    }
}
