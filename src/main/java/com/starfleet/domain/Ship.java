package com.starfleet.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Ship {
    private Coordinate2D coordinate;
    private int totalMoves;

    public void applyMove(Coordinate2D coordinate) {
        this.coordinate.setX(this.coordinate.getX() + coordinate.getX());
        this.coordinate.setY(this.coordinate.getY() + coordinate.getY());
        totalMoves++;
    }
}
