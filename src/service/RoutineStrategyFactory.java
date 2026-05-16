package service;

/**
 * GOF Factory Pattern: creates correct skin strategy.
 */
public class RoutineStrategyFactory {
    public static RoutineStrategy getStrategy(String skinType) {
        if (skinType == null) {
            return new NormalSkinStrategy();
        }
        switch (skinType.toLowerCase()) {
            case "oily":
                return new OilySkinStrategy();
            case "dry":
                return new DrySkinStrategy();
            case "combination":
                return new CombinationSkinStrategy();
            case "sensitive":
                return new SensitiveSkinStrategy();
            default:
                return new NormalSkinStrategy();
        }
    }
}
