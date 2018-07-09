package com.starfleet.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Coordinate3D {
    private int x;
    private int y;
    private int z;
    private boolean isMine;

    public int convertMineCharToZAxisValue(char mineChar) {
        return ((int)mineChar-(int)'`') * -1;
    }

    public char convertZAxisValueToMineChar(int mineZAxisValue) {
        if(mineZAxisValue == 0) {
            return '*';
        }
        return (char) ((mineZAxisValue * -1) + (int)'`');
    }
}
