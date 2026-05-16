package service;

import model.RoutineStep;
import model.SkinProfile;

import java.util.List;

public interface RoutineStrategy {
    List<RoutineStep> generateMorning(SkinProfile profile);

    List<RoutineStep> generateEvening(SkinProfile profile);
}
