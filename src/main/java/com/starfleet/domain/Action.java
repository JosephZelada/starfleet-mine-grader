package com.starfleet.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Action {
    public Direction direction;
    public FiringPattern firingPattern;

    @Override
    public String toString() {
        String resultString = "";
        if(firingPattern != null) {
            resultString += firingPattern.name() + " ";
        }
        if(direction != null) {
            resultString += direction.name() + " ";
        }
        resultString = resultString.toLowerCase();
        return resultString;
    }
}
