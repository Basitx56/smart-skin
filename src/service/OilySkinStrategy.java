package service;

import model.RoutineStep;
import model.SkinProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Routine strategy for oily skin type.
 * GOF Strategy Pattern concrete implementation.
 * Uses real dermatology-recommended ingredients.
 */
public class OilySkinStrategy implements RoutineStrategy {

    @Override
    public List<RoutineStep> generateMorning(SkinProfile profile) {
        List<RoutineStep> steps = new ArrayList<>();
        steps.add(new RoutineStep(1, "Cleanse",
            "Salicylic Acid Cleanser",
            "CeraVe Foaming Cleanser",
            "Removes excess oil without stripping the skin barrier"));
        steps.add(new RoutineStep(2, "Tone",
            "Niacinamide Toner",
            "Paula's Choice BHA Toner",
            "Controls sebum and minimizes pores"));
        steps.add(new RoutineStep(3, "Treat",
            "Niacinamide 10% Serum",
            "The Ordinary Niacinamide",
            "Reduces pore appearance and controls oil production"));
        steps.add(new RoutineStep(4, "Moisturize",
            "Oil-Free Gel Moisturizer",
            "Neutrogena Hydro Boost",
            "Hydrates without adding excess oil"));
        steps.add(new RoutineStep(5, "Protect",
            "SPF 50 Sunscreen",
            "EltaMD UV Clear SPF 46",
            "Protects and controls shine"));
        return steps;
    }

    @Override
    public List<RoutineStep> generateEvening(SkinProfile profile) {
        List<RoutineStep> steps = new ArrayList<>();
        steps.add(new RoutineStep(1, "Cleanse",
            "Gentle Foaming Cleanser",
            "La Roche-Posay Effaclar",
            "Deep cleans pores after the day"));
        steps.add(new RoutineStep(2, "Exfoliate",
            "AHA/BHA Exfoliant",
            "Paula's Choice 2% BHA",
            "Unclogs pores and removes dead skin"));
        steps.add(new RoutineStep(3, "Treat",
            "Retinol 0.5%",
            "CeraVe Resurfacing Retinol",
            "Reduces acne and improves texture"));
        steps.add(new RoutineStep(4, "Moisturize",
            "Lightweight Gel Cream",
            "Belif Aqua Bomb",
            "Provides moisture without heaviness"));
        steps.add(new RoutineStep(5, "Spot Treat",
            "Benzoyl Peroxide Spot Treatment",
            "Proactiv Emergency Blemish Relief",
            "Targets active breakouts overnight"));
        return steps;
    }
}
