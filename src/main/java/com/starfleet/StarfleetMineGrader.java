package com.starfleet;

import com.starfleet.service.GradingService;

public class StarfleetMineGrader {

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Usage: Specify a field and actions text file in the fields/actions order. Paths that don't start with a / will be assumed to be relative to jar location");
            return;
        }
        if(args.length != 2) {
            System.out.println("Incorrect number of parameters specified. Saw " + args.length +  " , but expected 2");
            return;
        }
        StringBuilder fieldFileName = new StringBuilder();
        fieldFileName.append(args[0]);
        StringBuilder actionsFileName = new StringBuilder();
        actionsFileName.append(args[1]);
        if(fieldFileName.charAt(0) != '/') {
            fieldFileName.insert(0, "./");
        }
        if(actionsFileName.charAt(0) != '/') {
            actionsFileName.insert(0, "./");
        }

        GradingService gradingService = new GradingService(fieldFileName.toString(), actionsFileName.toString());
        gradingService.gradeMineStrategy();
    }
}
