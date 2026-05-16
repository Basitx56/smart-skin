package service;

import model.RoutineStep;
import model.SkinProfile;

import java.util.ArrayList;
import java.util.List;

public class NormalSkinStrategy implements RoutineStrategy {
    @Override
    public List<RoutineStep> generateMorning(SkinProfile profile) {
        List<RoutineStep> steps = new ArrayList<>();
        steps.add(new RoutineStep(1, "Cleanse",
            "Gentle Cleanser",
            "CeraVe Hydrating Cream-to-Foam Cleanser",
            "Cleanses while maintaining skin balance"));
        steps.add(new RoutineStep(2, "Treat",
            "Vitamin C Serum",
            "Timeless 20% Vitamin C + E + Ferulic",
            "Antioxidant defense and tone support"));
        steps.add(new RoutineStep(3, "Hydrate",
            "Hyaluronic Acid Serum",
            "The Inkey List Hyaluronic Acid",
            "Boosts hydration and elasticity"));
        steps.add(new RoutineStep(4, "Protect",
            "Moisturizer with SPF",
            "CeraVe AM Facial Moisturizing Lotion SPF 30",
            "Combined hydration and daytime UV protection"));
        return steps;
    }

    @Override
    public List<RoutineStep> generateEvening(SkinProfile profile) {
        List<RoutineStep> steps = new ArrayList<>();
        steps.add(new RoutineStep(1, "Cleanse",
            "Gentle Cleanser",
            "La Roche-Posay Toleriane Cleanser",
            "Removes impurities without over-drying"));
        steps.add(new RoutineStep(2, "Treat",
            "Retinol Serum",
            "Olay Retinol24 Night Serum",
            "Supports cell turnover and smooth texture"));
        steps.add(new RoutineStep(3, "Repair",
            "Peptide Serum",
            "The Ordinary Multi-Peptide + HA",
            "Helps skin resilience and firmness"));
        steps.add(new RoutineStep(4, "Moisturize",
            "Night Moisturizer",
            "CeraVe PM Facial Moisturizing Lotion",
            "Locks in moisture overnight"));
        return steps;
    }
}
