package service;

import model.RoutineStep;
import model.SkinProfile;

import java.util.ArrayList;
import java.util.List;

public class DrySkinStrategy implements RoutineStrategy {
    @Override
    public List<RoutineStep> generateMorning(SkinProfile profile) {
        List<RoutineStep> steps = new ArrayList<>();
        steps.add(new RoutineStep(1, "Cleanse",
            "Cream Cleanser",
            "CeraVe Hydrating Cleanser",
            "Cleanses while preserving natural lipids"));
        steps.add(new RoutineStep(2, "Tone",
            "Hydrating Toner",
            "Klairs Supple Preparation Toner",
            "Adds lightweight hydration before treatment"));
        steps.add(new RoutineStep(3, "Treat",
            "Hyaluronic Acid Serum",
            "Vichy Minéral 89",
            "Improves moisture retention and skin plumpness"));
        steps.add(new RoutineStep(4, "Moisturize",
            "Rich Moisturizer",
            "La Roche-Posay Toleriane Riche",
            "Restores barrier comfort through the day"));
        steps.add(new RoutineStep(5, "Protect",
            "Broad Spectrum SPF",
            "Bioderma Photoderm Cream SPF 50+",
            "Protects dehydrated skin from UV damage"));
        return steps;
    }

    @Override
    public List<RoutineStep> generateEvening(SkinProfile profile) {
        List<RoutineStep> steps = new ArrayList<>();
        steps.add(new RoutineStep(1, "Cleanse",
            "Oil Cleanser",
            "Hada Labo Cleansing Oil",
            "Dissolves sunscreen and makeup gently"));
        steps.add(new RoutineStep(2, "Treat",
            "Hydrating Essence",
            "COSRX Snail Mucin Essence",
            "Enhances hydration and barrier recovery"));
        steps.add(new RoutineStep(3, "Treat",
            "Retinol 0.2%",
            "Avene RetrinAL",
            "Supports smooth texture with lower irritation"));
        steps.add(new RoutineStep(4, "Moisturize",
            "Ceramide Cream",
            "CeraVe Moisturizing Cream",
            "Replenishes ceramides overnight"));
        steps.add(new RoutineStep(5, "Seal",
            "Facial Oil",
            "The Ordinary 100% Squalane",
            "Seals in moisture and reduces transepidermal water loss"));
        return steps;
    }
}
