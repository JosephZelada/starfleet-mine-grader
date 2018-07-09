package com.starfleet.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class FieldHelper {
    @Getter
    private Ship ship;

    boolean didShotCollideWithMine(Coordinate3D coordinate3D, Coordinate2D coordinate2D) {
        return coordinate3D.isMine() && coordinate3D.getX() == coordinate2D.getX() + ship.getCoordinate().getX();
    }

    char convertCoordinate3DToChar(Coordinate3D coordinate3D) {
        if(!coordinate3D.isMine()) {
            return '.';
        } else {
            return coordinate3D.convertZAxisValueToMineChar(coordinate3D.getZ());
        }
    }

    int calculateYAxisRelativeToShip(Coordinate2D coordinate2D) {
        return coordinate2D.getY() + ship.getCoordinate().getY();
    }

    int calculateXAxisPaddingFromShip(List<Coordinate2D> coordinate2DList) {
        int greatestXDistanceIndex = 0;
        int negativeXDistance = Math.abs(coordinate2DList.get(1).getX() - ship.getCoordinate().getX());
        int positiveXDistance = Math.abs(coordinate2DList.get(0).getX() - ship.getCoordinate().getX());
        if(positiveXDistance == negativeXDistance) {
            return 0;

        } else if (positiveXDistance < negativeXDistance) {
            greatestXDistanceIndex = 1;
        }
        return (coordinate2DList.get(greatestXDistanceIndex).getX() - ship.getCoordinate().getX()) * -1;
    }

    int calculateYAxisPaddingFromShip(List<Coordinate2D> coordinate2DList) {
        int greatestYDistanceIndex = 0;
        int negativeYDistance = Math.abs(coordinate2DList.get(1).getY() - ship.getCoordinate().getY());
        int positiveYDistance = Math.abs(coordinate2DList.get(0).getY() - ship.getCoordinate().getY());
        if(positiveYDistance == negativeYDistance) {
            return 0;

        } else if (positiveYDistance < negativeYDistance) {
            greatestYDistanceIndex = 1;
        }
        return (coordinate2DList.get(greatestYDistanceIndex).getY() - ship.getCoordinate().getY()) * -1;
    }

    List<Coordinate2D> getDisplayBoundaryCoordinatesFromField(Map<Integer, List<Coordinate3D>> fieldValues) {
        int highestXAxisMine = ship.getCoordinate().getX();
        int lowestXAxisMine = ship.getCoordinate().getX();
        int highestYAxisMine = ship.getCoordinate().getY();
        int lowestYAxisMine = ship.getCoordinate().getY();
        for(Integer index: fieldValues.keySet()) {
            for(Coordinate3D coordinate3D : fieldValues.get(index)) {
                if(coordinate3D.isMine()) {
                    if (coordinate3D.getY() > highestYAxisMine) {
                        highestYAxisMine = coordinate3D.getY();
                    }
                    if (coordinate3D.getY() < lowestYAxisMine) {
                        lowestYAxisMine = coordinate3D.getY();
                    }
                    if (coordinate3D.getX() > highestXAxisMine) {
                        highestXAxisMine = coordinate3D.getX();
                    }
                    if (coordinate3D.getX() < lowestXAxisMine) {
                        lowestXAxisMine = coordinate3D.getX();
                    }
                }
            }
        }
        List<Coordinate2D> coordinateBoundariesList = new ArrayList<>();
        coordinateBoundariesList.add(new Coordinate2D(highestXAxisMine, highestYAxisMine));
        coordinateBoundariesList.add(new Coordinate2D(lowestXAxisMine, lowestYAxisMine));
        return coordinateBoundariesList;
    }

    int calculateXAxisSpaceAlreadyPresent(List<Coordinate2D> coordinateBoundariesList, int xOffset) {
        int spaceGridsAlreadyPresent = 0;
        if(xOffset < 0) {
            spaceGridsAlreadyPresent = Math.abs(coordinateBoundariesList.get(1).getX() - ship.getCoordinate().getX());
        } else if (xOffset > 0) {
            spaceGridsAlreadyPresent = Math.abs(coordinateBoundariesList.get(0).getX() - ship.getCoordinate().getX());
        }
        return spaceGridsAlreadyPresent;
    }

    int calculateYAxisSpaceAlreadyPresent(List<Coordinate2D> coordinateBoundariesList, int yOffset) {
        int spaceGridsAlreadyPresent = 0;
        if(yOffset < 0) {
            spaceGridsAlreadyPresent = Math.abs(coordinateBoundariesList.get(1).getY() - ship.getCoordinate().getY());
        } else if (yOffset > 0) {
            spaceGridsAlreadyPresent = Math.abs(coordinateBoundariesList.get(0).getY() - ship.getCoordinate().getY());
        }
        return spaceGridsAlreadyPresent;
    }
}
