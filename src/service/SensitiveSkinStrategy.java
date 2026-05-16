package service;

import model.RoutineStep;
import model.SkinProfile;

import java.util.ArrayList;
import java.util.List;

public class SensitiveSkinStrategy implements RoutineStrategy {
    @Override
    public List<RoutineStep> generateMorning(SkinProfile profile) {
        List<RoutineStep> steps = new ArrayList<>();
        steps.add(new RoutineStep(1, "Cleanse",
            "Fragrance-Free Cleanser",
            "Vanicream Gentle Facial Cleanser",
            "Minimizes irritation while cleansing"));
        steps.add(new RoutineStep(2, "Tone",
            "Calming Toner",
            "Etude SoonJung pH 5.5 Relief Toner",
            "Soothes redness and supports barrier pH"));
        steps.add(new RoutineStep(3, "Treat",
            "Centella Serum",
            "SKIN1004 Centella Ampoule",
            "Reduces visible irritation and discomfort"));
        steps.add(new RoutineStep(4, "Moisturize",
            "Soothing Moisturizer",
            "La Roche-Posay Toleriane Dermallergo",
            "Hydrates and reinforces sensitive skin barrier"));
        steps.add(new RoutineStep(5, "Protect",
            "Mineral SPF",
            "EltaMD UV Physical SPF 41",
            "Provides broad protection with zinc oxide"));
        return steps;
    }

    @Override
    public List<RoutineStep> generateEvening(SkinProfile profile) {
        List<RoutineStep> steps = new ArrayList<>();
        steps.add(new RoutineStep(1, "Cleanse",
            "Gentle Cleanser",
            "Avene Extremely Gentle Cleanser",
            "Low-residue cleanse for reactive skin"));
        steps.add(new RoutineStep(2, "Calm",
            "Thermal Water Mist",
            "Avene Thermal Spring Water",
            "Instant soothing for irritation-prone skin"));
        steps.add(new RoutineStep(3, "Treat",
            "Bakuchiol Serum",
            "Bybi Bakuchiol Booster",
            "Retinol alternative with lower irritation potential"));
        steps.add(new RoutineStep(4, "Moisturize",
            "Barrier Cream",
            "Cicaplast Baume B5",
            "Supports overnight barrier repair"));
        return steps;
    }
}
