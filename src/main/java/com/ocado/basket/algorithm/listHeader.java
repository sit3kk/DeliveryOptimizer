package com.ocado.basket.algorithm;

public class listHeader extends columnObject {
    Object data;
    int size;

    public listHeader(columnObject up, columnObject down, columnObject right, columnObject left, Object data) {
        super(up, down, right, left, null);
        this.data = data;
        this.size = 0;
    }

    public String toString() {
        return data.toString();
    }
}
