package com.starfleet.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FiringPatternMap {
    @Getter
    private static Map<String, List<Coordinate2D>> firingPatternMap = initFiringPatternMap();

    private static Map<String, List<Coordinate2D>> initFiringPatternMap() {
        Map <String, List<Coordinate2D>> currentFiringPatternMap = new HashMap<>();
        List<Coordinate2D> alphaFiringPatternShotList = new ArrayList<>();
        alphaFiringPatternShotList.add(new Coordinate2D(-1, -1));
        alphaFiringPatternShotList.add(new Coordinate2D(-1, 1));
        alphaFiringPatternShotList.add(new Coordinate2D(1, -1));
        alphaFiringPatternShotList.add(new Coordinate2D(1, 1));
        currentFiringPatternMap.put(FiringPattern.ALPHA.name(), alphaFiringPatternShotList);

        List<Coordinate2D>betaFiringPatternShotList = new ArrayList<>();
        betaFiringPatternShotList.add(new Coordinate2D(-1, 0));
        betaFiringPatternShotList.add(new Coordinate2D(0, -1));
        betaFiringPatternShotList.add(new Coordinate2D(0, 1));
        betaFiringPatternShotList.add(new Coordinate2D(1, 0));
        currentFiringPatternMap.put(FiringPattern.BETA.name(), betaFiringPatternShotList);

        List<Coordinate2D>gammaFiringPatternShotList = new ArrayList<>();
        gammaFiringPatternShotList.add(new Coordinate2D(-1, 0));
        gammaFiringPatternShotList.add(new Coordinate2D(0, 0));
        gammaFiringPatternShotList.add(new Coordinate2D(1, 0));
        currentFiringPatternMap.put(FiringPattern.GAMMA.name(), gammaFiringPatternShotList);

        List<Coordinate2D>deltaFiringPatternShotList = new ArrayList<>();
        deltaFiringPatternShotList.add(new Coordinate2D(0, -1));
        deltaFiringPatternShotList.add(new Coordinate2D(0, 0));
        deltaFiringPatternShotList.add(new Coordinate2D(0, 1));
        currentFiringPatternMap.put(FiringPattern.DELTA.name(), deltaFiringPatternShotList);

        return currentFiringPatternMap;
    }
}
