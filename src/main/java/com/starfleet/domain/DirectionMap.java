package com.starfleet.domain;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class DirectionMap {
    @Getter
    private static Map<String, Coordinate2D> directionMap = initDirectionMap();

    private static Map<String, Coordinate2D> initDirectionMap() {
        Map<String, Coordinate2D> map = new HashMap<>();
        map.put(Direction.EAST.name(), new Coordinate2D(1, 0));
        map.put(Direction.WEST.name(), new Coordinate2D(-1, 0));
        map.put(Direction.NORTH.name(), new Coordinate2D(0, 1));
        map.put(Direction.SOUTH.name(), new Coordinate2D(0, -1));
        return map;
    }
}
