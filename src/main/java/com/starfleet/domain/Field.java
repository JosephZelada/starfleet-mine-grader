package com.starfleet.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Field {
    private Map<Integer, List<Coordinate3D>> fieldValues;

    private int totalMines;
    private int minesLeft;
    private int totalShots;
    private boolean shipHasMissedAMine;
    private FieldHelper fieldHelper;

    public void getBoundariesAndCleanUpFieldMap() {
        List<Coordinate2D> coordinate2DList = fieldHelper.getDisplayBoundaryCoordinatesFromField(fieldValues);
        cleanUpFieldValuesMap(coordinate2DList);
    }

    public int getPointsEarned(int movesLeft) {
        if(minesLeft != 0 || shipHasMissedAMine) {
            return 0;
        }
        if(movesLeft > 0) {
            return 1;
        }
        int minesPointsVar = totalMines;
        int shotPointsVar = totalShots;
        int movePointsVar = fieldHelper.getShip().getTotalMoves();
        if (shotPointsVar > minesPointsVar) {
            shotPointsVar = minesPointsVar;
        }
        if (movePointsVar > minesPointsVar * 1.5) {
            movePointsVar = (int)(minesPointsVar * 1.5);
        }
        return 10*minesPointsVar-5*shotPointsVar-2*movePointsVar;
    }

    public void dropLevelForMineCoordinates() {
        for(Integer index: fieldValues.keySet()) {
            List<Coordinate3D> currentCoordinate3DList = new ArrayList<>();
            for(Coordinate3D coordinate3D : fieldValues.get(index)) {
                if(coordinate3D.isMine()) {
                    coordinate3D.setZ(coordinate3D.getZ() + 1);
                }
                currentCoordinate3DList.add(coordinate3D);
            }
            fieldValues.put(index, currentCoordinate3DList);
        }
    }

    public void applyFiringPattern(String firingPatternName) {
        List<Coordinate2D> coordinate2DList = FiringPatternMap.getFiringPatternMap().get(firingPatternName.toUpperCase());
        for(Coordinate2D coordinate2D : coordinate2DList) {
            applySingleShot(coordinate2D);
        }
    }

    public boolean isShipOnSameLevelAsMine() {
        for(Integer index: fieldValues.keySet()) {
            for(Coordinate3D coordinate3D : fieldValues.get(index)) {
                if(coordinate3D.isMine() && coordinate3D.getZ() == 0) {
                    shipHasMissedAMine = true;
                    return true;
                }
            }
        }
        return false;
    }

    public String getFieldRelativeToShip() {
        List<Coordinate2D> coordinateBoundariesList = fieldHelper.getDisplayBoundaryCoordinatesFromField(fieldValues);
        int xAxisPaddingCount = fieldHelper.calculateXAxisPaddingFromShip(coordinateBoundariesList);
        String fieldString = createEmptySpacePaddingDisplayStrings(xAxisPaddingCount, coordinateBoundariesList);
        fieldString += createDisplayedGridFromFieldMapValues(coordinateBoundariesList, xAxisPaddingCount);
        return fieldString ;
    }

    private String createDisplayedGridFromFieldMapValues(List<Coordinate2D> coordinateBoundariesList, int xAxisPaddingCount) {
        List<Integer> keyList = new ArrayList<>(fieldValues.keySet());
        keyList.sort(Collections.reverseOrder());

        StringBuilder fieldString = new StringBuilder();
        for(Integer index: keyList) {
            List<Coordinate3D> coordinate3DList = fieldValues.get(index);
            fieldString.append(createStringForFieldLine(coordinate3DList,
                                                    coordinateBoundariesList,
                                                    xAxisPaddingCount));
        }

        return fieldString.toString();
    }

    private String createEmptySpacePaddingDisplayStrings(int xAxisPaddingCount, List<Coordinate2D> coordinateBoundariesList) {
        StringBuilder fieldExtraYAxisString = new StringBuilder();
        List<Coordinate3D> extraDisplayCoordinate3D = new ArrayList<>();
        int yAxisPaddingCount = fieldHelper.calculateYAxisPaddingFromShip(coordinateBoundariesList);
        int fieldIndex = (int)fieldValues.keySet().toArray()[0];
        int xAxisCurrentDisplaySizeWithoutPadding = fieldValues.get(fieldIndex).size();
        for(int i = 0; i < calculateEmptySpacePaddingXAxisLength(xAxisPaddingCount, xAxisCurrentDisplaySizeWithoutPadding); i++) {
            extraDisplayCoordinate3D.add(new Coordinate3D(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, false));
        }
        int spaceGridsAlreadyPresent = fieldHelper.calculateYAxisSpaceAlreadyPresent(coordinateBoundariesList, yAxisPaddingCount);
        for(int i = 0; i < Math.abs(yAxisPaddingCount) - spaceGridsAlreadyPresent; i++) {
            fieldExtraYAxisString.append(createStringForFieldLine(extraDisplayCoordinate3D,
                                                              coordinateBoundariesList,
                                                              xAxisPaddingCount
            ));
        }
        return fieldExtraYAxisString.toString();
    }

    private int calculateEmptySpacePaddingXAxisLength(int xAxisPaddingCount, int defaultCoordinateLength) {
        if(xAxisPaddingCount > 0) {
            return Math.abs(xAxisPaddingCount * 2) + 1;
        }
        return defaultCoordinateLength;
    }

    private String createStringForFieldLine(List<Coordinate3D> coordinate3DList, List<Coordinate2D> coordinateBoundariesList, int xOffset) {
        StringBuilder fieldString = new StringBuilder();
        String emptySpaceStringToAppend = createPartialEmptySpacePaddingDisplayString(coordinate3DList, coordinateBoundariesList, xOffset);
        for(Coordinate3D coordinate3D : coordinate3DList) {
            fieldString.append(fieldHelper.convertCoordinate3DToChar(coordinate3D));
        }
        if(xOffset > 0) {
            fieldString.append(emptySpaceStringToAppend);
        } else {
            fieldString.insert(0, emptySpaceStringToAppend);
        }
        fieldString.append("\n");
        return fieldString.toString();
    }

    private String createPartialEmptySpacePaddingDisplayString(List<Coordinate3D> coordinate3DList, List<Coordinate2D> coordinateBoundariesList, int xOffset) {
        StringBuilder emptySpaceStringToAppend = new StringBuilder();
        int spaceGridsAlreadyPresent = fieldHelper.calculateXAxisSpaceAlreadyPresent(coordinateBoundariesList, xOffset);
        for(int i = 0; i < Math.abs(xOffset) - spaceGridsAlreadyPresent; i++) {
            emptySpaceStringToAppend.append('.');
        }
        if((emptySpaceStringToAppend.length() + coordinate3DList.size()) % 2 == 0) {
            emptySpaceStringToAppend.append('.');
        }
        return emptySpaceStringToAppend.toString();
    }

    private void applySingleShot(Coordinate2D coordinate2D) {
        List<Coordinate3D> coordinate3DList = fieldValues.get(fieldHelper.calculateYAxisRelativeToShip(coordinate2D));
        if(coordinate3DList != null) {
            for(Coordinate3D coordinate3D : coordinate3DList) {
                if(fieldHelper.didShotCollideWithMine(coordinate3D, coordinate2D)){
                    coordinate3D.setMine(false);
                    coordinate3D.setZ(1);
                    minesLeft--;
                }
            }
        }
        totalShots++;
    }

    private void cleanUpFieldValuesMap(List<Coordinate2D> coordinateBoundaries) {
        int highestXAxisMine = coordinateBoundaries.get(0).getX();
        int lowestXAxisMine = coordinateBoundaries.get(1).getX();
        int highestYAxisMine = coordinateBoundaries.get(0).getY();
        int lowestYAxisMine = coordinateBoundaries.get(1).getY();

        List<Integer> rowsToDelete = new ArrayList<>();
        for(Integer index: fieldValues.keySet()) {
            if(index > highestYAxisMine || index < lowestYAxisMine) {
                rowsToDelete.add(index);
                continue;
            }
            List<Coordinate3D> currentCoordinate3DList = new ArrayList<>();
            for(Coordinate3D coordinate3D : fieldValues.get(index)) {
                if(coordinate3D.getX() <= highestXAxisMine && coordinate3D.getX() >= lowestXAxisMine) {
                    currentCoordinate3DList.add(coordinate3D);
                }
            }
            fieldValues.put(index, currentCoordinate3DList);
        }
        for(Integer index: rowsToDelete) {
            fieldValues.remove(index);
        }
    }
}
