package com.ocado.basket.algorithm;

public class columnObject {
    public columnObject up;
    public columnObject down;
    public columnObject right;
    public columnObject left;
    public listHeader columnHead;
    int i;
    int j;

    columnObject(columnObject up, columnObject down, columnObject right, columnObject left, listHeader columnHead) {
        this.up = up;
        this.down = down;
        this.right = right;
        this.left = left;
        this.columnHead = columnHead;
    }

    public String toString() {
        return String.format("%s,%s", i, j);
    }

}