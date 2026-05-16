package service;

import model.RoutineStep;
import model.SkinProfile;

import java.util.ArrayList;
import java.util.List;

public class CombinationSkinStrategy implements RoutineStrategy {
    @Override
    public List<RoutineStep> generateMorning(SkinProfile profile) {
        List<RoutineStep> steps = new ArrayList<>();
        steps.add(new RoutineStep(1, "Cleanse",
            "Gentle Cleanser",
            "Cetaphil Gentle Skin Cleanser",
            "Balances cleansing for both oily and dry zones"));
        steps.add(new RoutineStep(2, "Tone",
            "Balancing Toner",
            "Round Lab Dokdo Toner",
            "Hydrates dry areas while staying light on T-zone"));
        steps.add(new RoutineStep(3, "Treat",
            "Vitamin C Serum",
            "La Roche-Posay Pure Vitamin C10",
            "Brightens tone and supports antioxidant defense"));
        steps.add(new RoutineStep(4, "Moisturize",
            "Lightweight Moisturizer",
            "Neutrogena Hydro Boost Water Gel",
            "Hydrates without heaviness"));
        steps.add(new RoutineStep(5, "Protect",
            "SPF 50 Sunscreen",
            "Isntree Hyaluronic Acid Watery Sun Gel",
            "Daily broad-spectrum UV protection"));
        return steps;
    }

    @Override
    public List<RoutineStep> generateEvening(SkinProfile profile) {
        List<RoutineStep> steps = new ArrayList<>();
        steps.add(new RoutineStep(1, "Cleanse",
            "Micellar Cleanser",
            "Bioderma Sensibio H2O",
            "Removes buildup while keeping cheeks comfortable"));
        steps.add(new RoutineStep(2, "Tone",
            "BHA Toner",
            "COSRX BHA Blackhead Power Liquid",
            "Targets congestion-prone areas"));
        steps.add(new RoutineStep(3, "Treat",
            "Niacinamide Serum",
            "The Ordinary Niacinamide 10%",
            "Balances sebum and refines pores"));
        steps.add(new RoutineStep(4, "Moisturize",
            "Gel-Cream Moisturizer",
            "Belif The True Cream Aqua Bomb",
            "Hydrates with a balanced finish"));
        steps.add(new RoutineStep(5, "Target",
            "Eye Cream",
            "CeraVe Eye Repair Cream",
            "Supports delicate under-eye skin overnight"));
        return steps;
    }
}
