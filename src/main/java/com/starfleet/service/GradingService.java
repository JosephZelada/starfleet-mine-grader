package com.starfleet.service;

import com.starfleet.domain.Action;
import com.starfleet.domain.Coordinate2D;
import com.starfleet.domain.Coordinate3D;
import com.starfleet.domain.Direction;
import com.starfleet.domain.DirectionMap;
import com.starfleet.domain.Field;
import com.starfleet.domain.FieldHelper;
import com.starfleet.domain.FiringPattern;
import com.starfleet.domain.FiringPatternMap;
import com.starfleet.domain.Ship;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class GradingService {

    private Field field;
    private List<Action> actionList;
    private Ship ship;
    private int totalMines;

    public GradingService(String fieldFileName, String actionsFileName) {
        this.ship = new Ship(new Coordinate2D(0, 0), 0);
        this.totalMines = 0;
        parseAndStoreFieldAndActionFiles(fieldFileName, actionsFileName);
    }

    public void gradeMineStrategy() {
        int stepNumber = 0;
        for(Action action: actionList) {
            stepNumber++;
            printHeaderAndCurrentFieldStateWithAction(stepNumber, action);
            applyActionsAndMovements(action);
            performPostActionPrintEvaluationAndCleanup();
            if(field.isShipOnSameLevelAsMine() || noMinesLeftOnField(field)) {
                break;
            }
        }
        assessPointsEarnedAndPrint(stepNumber);
    }

    private void assessPointsEarnedAndPrint(int stepNumber) {
        int pointsEarned = field.getPointsEarned(actionList.size() - stepNumber);
        String status = "pass";
        if (pointsEarned == 0) {
            status = "fail";
        }
        System.out.printf("%s (%s)%n", status, pointsEarned);
    }

    private void performPostActionPrintEvaluationAndCleanup() {
        field.getBoundariesAndCleanUpFieldMap();
        field.dropLevelForMineCoordinates();
        System.out.println(field.getFieldRelativeToShip());
    }

    private void printHeaderAndCurrentFieldStateWithAction(int stepNumber, Action action) {
        System.out.printf("Step %s %n%n", stepNumber);
        System.out.println(field.getFieldRelativeToShip());
        System.out.println(action + "\n");
    }

    private void applyActionsAndMovements(Action action) {
        if(action.getFiringPattern() != null) {
            field.applyFiringPattern(action.getFiringPattern().name());
        }
        if(action.getDirection() != null) {
            this.ship.applyMove(DirectionMap.getDirectionMap().get(action.getDirection().name()));
        }
    }

    private boolean noMinesLeftOnField(Field field) {
        return field.getFieldValues().size() == 1 && field.getMinesLeft() == 0;
    }

    private void parseAndStoreFieldAndActionFiles(String fieldFileName, String actionsFileName) {
        field = parseFieldFile(fieldFileName);
        actionList = parseActionsFile(actionsFileName);
    }

    private Field parseFieldFile(String fieldFileName) {
        BufferedReader bufferedReader = initBufferedReader(fieldFileName);
        BufferedReader lineCountBufferedReader = initBufferedReader(fieldFileName);
        if(bufferedReader == null || lineCountBufferedReader == null) {
            return null;
        }
        Map<Integer, List<Coordinate3D>> fieldMap = new HashMap<>();
        Stream<String> fieldFileStream = bufferedReader.lines();
        int totalLines = (int)lineCountBufferedReader.lines().count();
        AtomicInteger currentYAxis = new AtomicInteger(totalLines/2);
        fieldFileStream.forEach(fieldLine -> {
            fieldMap.put(currentYAxis.get(), parseFieldFileLine(fieldLine, currentYAxis.get()));
            currentYAxis.getAndDecrement();
        });
        return Field.builder()
                .fieldValues(fieldMap)
                .totalMines(totalMines)
                .minesLeft(totalMines)
                .fieldHelper(new FieldHelper(ship))
                .build();
    }

    private List<Action> parseActionsFile(String actionsFileName) {
        BufferedReader bufferedReader = initBufferedReader(actionsFileName);
        if(bufferedReader == null) {
            return null;
        }
        List<Action> actionList = new ArrayList<>();
        bufferedReader.lines().forEach(actionsLine -> {
            actionList.add(parseActionsFileLine(actionsLine));
        });
        return actionList;
    }

    private Action parseActionsFileLine(String actionsLine) {
        String[] fields = actionsLine.split("\\s+|\\t+");
        boolean actionsLineContainsFiringPattern = FiringPatternMap.getFiringPatternMap().containsKey(fields[0].toUpperCase());
        if((!actionsLineContainsFiringPattern && fields.length == 2) || (fields.length != 1 && fields.length != 2)) {
            System.out.println("Actions line is invalid: " + actionsLine + ", this line will be ignored and count against your score");
            return new Action();
        }
        int fieldIndex;
        if(fields.length == 1) {
            fieldIndex = 0;
        } else {
            fieldIndex = 1;
        }
        boolean actionsLineContainsDirection = DirectionMap.getDirectionMap().containsKey(fields[fieldIndex].toUpperCase());
        Action action = new Action();
        if(actionsLineContainsDirection) {
            int fieldIndexAlt;
            if(fields.length == 1) {
                fieldIndexAlt = 0;
            } else {
                fieldIndexAlt = 1;
            }
            action.setDirection(Direction.valueOf(fields[fieldIndexAlt].toUpperCase()));
        }
        if(actionsLineContainsFiringPattern) {
            action.setFiringPattern(FiringPattern.valueOf(fields[0].toUpperCase()));
        }
        return action;
    }

    private List<Coordinate3D> parseFieldFileLine(String fieldLine, int yAxis) {
        List<Coordinate3D> currentMineList = new ArrayList<>();
        int currentXAxisCoordinate = 0 - fieldLine.length()/2;
        for(char coordinateChar: fieldLine.toCharArray()) {
            Coordinate3D currentCoordinate3D = new Coordinate3D(currentXAxisCoordinate, yAxis, 1, false);
            if(coordinateChar != '.') {
                currentCoordinate3D.setMine(true);
                currentCoordinate3D.setZ(currentCoordinate3D.convertMineCharToZAxisValue(coordinateChar));
                totalMines++;
            }
            currentMineList.add(currentCoordinate3D);
            currentXAxisCoordinate++;
        }
        return  currentMineList;
    }

    private BufferedReader initBufferedReader(String fileName) {
        File file = new File(fileName);
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            System.out.println("Received error reading file " + fileName + ", please check that the file exists and is accessible. Full stack trace: \n" + e);
            return null;
        }
        return bufferedReader;
    }
}
