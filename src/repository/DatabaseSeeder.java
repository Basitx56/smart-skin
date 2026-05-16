package repository;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseSeeder {

    private static final String[] AVAILABILITY_SLOTS = {
        "09:00", "10:00", "11:00", "14:00", "15:00"
    };

    public void seedDatabase() {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);

            String adminId = upsertUser(
                connection,
                "System Admin",
                "admin@skinapp.com",
                "Admin@123",
                "admin",
                null,
                null,
                false,
                true
            );

            String expertId = upsertUser(
                connection,
                "Dr. Sarah Ahmed",
                "expert@skinapp.com",
                "Expert@123",
                "expert",
                "Dermatology",
                50.0,
                true,
                true
            );

            String endUserId = upsertUser(
                connection,
                "Test User",
                "user@skinapp.com",
                "User@1234",
                "end_user",
                null,
                null,
                false,
                true
            );

            upsertSkinProfile(connection, endUserId);
            seedProducts(connection);
            seedConflictRules(connection);
            seedExpertAvailability(connection, expertId);
            seedCompletedAppointments(connection, endUserId, expertId);

            connection.commit();

            // Prevent unused variable warnings in strict analyzers.
            if (adminId == null) {
                throw new IllegalStateException("Admin creation failed");
            }
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
            }
            throw new RuntimeException("Database seeding failed: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

    private String upsertUser(Connection connection,
                              String name,
                              String email,
                              String plainPassword,
                              String role,
                              String specialization,
                              Double consultationFee,
                              boolean isVerified,
                              boolean isActive) throws SQLException {
        String existingId = findUserIdByEmail(connection, email);
        String hashedPassword = BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());

        if (existingId != null) {
            String updateSql = "UPDATE users SET name=?, password=?, role=?, specialization=?, consultationFee=?, isVerified=?, isActive=? WHERE userID=?";
            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setString(1, name);
                ps.setString(2, hashedPassword);
                ps.setString(3, role);
                ps.setString(4, specialization);
                if (consultationFee == null) {
                    ps.setNull(5, java.sql.Types.DOUBLE);
                } else {
                    ps.setDouble(5, consultationFee);
                }
                ps.setBoolean(6, isVerified);
                ps.setBoolean(7, isActive);
                ps.setString(8, existingId);
                ps.executeUpdate();
            }
            return existingId;
        }

        String userId = UUID.randomUUID().toString();
        String insertSql = "INSERT INTO users (userID, name, email, password, age, gender, role, specialization, consultationFee, rating, isVerified, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            ps.setString(1, userId);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, hashedPassword);
            ps.setInt(5, 0);
            ps.setString(6, "unknown");
            ps.setString(7, role);
            ps.setString(8, specialization);
            if (consultationFee == null) {
                ps.setNull(9, java.sql.Types.DOUBLE);
            } else {
                ps.setDouble(9, consultationFee);
            }
            ps.setDouble(10, 0.0);
            ps.setBoolean(11, isVerified);
            ps.setBoolean(12, isActive);
            ps.executeUpdate();
        }

        return userId;
    }

    private String findUserIdByEmail(Connection connection, String email) throws SQLException {
        String sql = "SELECT userID FROM users WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("userID");
                }
            }
        }
        return null;
    }

    private void upsertSkinProfile(Connection connection, String userId) throws SQLException {
        String findSql = "SELECT profileID FROM skin_profiles WHERE userID = ?";
        String existingProfileId = null;
        try (PreparedStatement ps = connection.prepareStatement(findSql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    existingProfileId = rs.getString("profileID");
                }
            }
        }

        if (existingProfileId == null) {
            String insertSql = "INSERT INTO skin_profiles (profileID, userID, skinType, skinConcerns, knownAllergies, currentProducts, diet, sleepHours, stressLevel, sunExposure, completenessPercentage) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                ps.setString(1, UUID.randomUUID().toString());
                ps.setString(2, userId);
                ps.setString(3, "oily");
                ps.setString(4, "acne,pigmentation");
                ps.setString(5, "fragrance,lanolin");
                ps.setString(6, "gel cleanser,oil-free moisturizer");
                ps.setString(7, "balanced");
                ps.setInt(8, 7);
                ps.setString(9, "medium");
                ps.setString(10, "moderate");
                ps.setInt(11, 80);
                ps.executeUpdate();
            }
        } else {
            String updateSql = "UPDATE skin_profiles SET skinType=?, skinConcerns=?, knownAllergies=?, completenessPercentage=?, updatedAt=NOW() WHERE profileID=?";
            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setString(1, "oily");
                ps.setString(2, "acne,pigmentation");
                ps.setString(3, "fragrance,lanolin");
                ps.setInt(4, 80);
                ps.setString(5, existingProfileId);
                ps.executeUpdate();
            }
        }
    }

    private void seedProducts(Connection connection) throws SQLException {
        List<String[]> products = buildProductSeeds();
        String existsSql = "SELECT COUNT(*) FROM products WHERE name=? AND brand=?";
        String insertSql = "INSERT INTO products (productID, name, brand, category, ingredients, suitableSkinTypes, price, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        for (String[] p : products) {
            boolean exists;
            try (PreparedStatement existsPs = connection.prepareStatement(existsSql)) {
                existsPs.setString(1, p[0]);
                existsPs.setString(2, p[1]);
                try (ResultSet rs = existsPs.executeQuery()) {
                    rs.next();
                    exists = rs.getInt(1) > 0;
                }
            }

            if (!exists) {
                try (PreparedStatement insertPs = connection.prepareStatement(insertSql)) {
                    insertPs.setString(1, UUID.randomUUID().toString());
                    insertPs.setString(2, p[0]);
                    insertPs.setString(3, p[1]);
                    insertPs.setString(4, p[2]);
                    insertPs.setString(5, p[3]);
                    insertPs.setString(6, p[4]);
                    insertPs.setDouble(7, Double.parseDouble(p[5]));
                    insertPs.setString(8, p[6]);
                    insertPs.executeUpdate();
                }
            }
        }
    }

    private List<String[]> buildProductSeeds() {
        List<String[]> products = new ArrayList<>();

        products.add(new String[]{"Night Renewal Retinol Serum", "DermaLeaf", "Serum", "retinol 0.3%, squalane, ceramides, vitamin E", "oily,combination,normal", "24.99", "Beginner-friendly retinol for texture and fine lines."});
        products.add(new String[]{"Retinol Recovery Cream", "SkinMend", "Moisturizer", "retinol 0.2%, panthenol, shea butter, allantoin", "dry,sensitive,normal", "27.50", "Barrier-support cream with low-strength retinol."});
        products.add(new String[]{"Advanced Retinol Night Gel", "PureDerm", "Gel", "retinol 0.5%, niacinamide, glycerin, bisabolol", "oily,combination", "29.00", "Oil-light gel targeting uneven texture."});
        products.add(new String[]{"Retinol Micro-Encapsulated Drops", "Clinesta", "Treatment", "encapsulated retinol 0.4%, hyaluronic acid, peptides", "all", "35.00", "Slow-release retinol to reduce irritation."});
        products.add(new String[]{"Retinol Calm Emulsion", "Aurea Skin", "Emulsion", "retinol 0.1%, centella asiatica, beta-glucan", "sensitive,normal,combination", "31.75", "Gentle retinol emulsion for first-time users."});
        products.add(new String[]{"Overnight Retinol Repair", "NovaCare", "Cream", "retinol 0.3%, ceramide NP, cholesterol, fatty acids", "dry,combination,normal", "33.20", "Overnight treatment focused on barrier and renewal."});

        products.add(new String[]{"Niacinamide 10 Clarifying Serum", "DermaLeaf", "Serum", "niacinamide 10%, zinc PCA, green tea extract", "oily,combination", "18.90", "Controls sebum and visible pores."});
        products.add(new String[]{"Niacinamide Barrier Booster", "SkinMend", "Serum", "niacinamide 5%, ceramides, panthenol", "dry,sensitive,normal", "21.40", "Supports barrier and redness balance."});
        products.add(new String[]{"Pore Balance Niacinamide Gel", "PureDerm", "Gel", "niacinamide 8%, allantoin, sodium PCA", "oily,combination,normal", "19.99", "Light gel for uneven tone and oil control."});
        products.add(new String[]{"Daily Tone Corrector", "Clinesta", "Lotion", "niacinamide 5%, tranexamic acid, licorice extract", "all", "26.00", "Brightening lotion for blotchy skin tone."});
        products.add(new String[]{"Niacinamide + Peptide Milk", "Aurea Skin", "Essence", "niacinamide 6%, peptides, betaine", "normal,dry,combination", "25.60", "Hydrating essence that smooths roughness."});
        products.add(new String[]{"Clear Complexion Booster", "NovaCare", "Ampoule", "niacinamide 12%, N-acetyl glucosamine, EGCG", "oily,combination", "28.80", "High-strength booster for stubborn congestion."});

        products.add(new String[]{"Vitamin C Bright Shot", "DermaLeaf", "Serum", "L-ascorbic acid 15%, ferulic acid, vitamin E", "normal,combination,oily", "30.00", "Antioxidant serum for dull skin and marks."});
        products.add(new String[]{"Radiance C Suspension", "SkinMend", "Cream", "ascorbyl glucoside 12%, hyaluronic acid, glycerin", "dry,normal,sensitive", "24.70", "Gentle vitamin C cream for daily glow."});
        products.add(new String[]{"C-Glow Water Essence", "PureDerm", "Essence", "sodium ascorbyl phosphate, panthenol, aloe vera", "all", "22.90", "Water-light vitamin C for sensitive users."});
        products.add(new String[]{"Antioxidant Day Concentrate", "Clinesta", "Serum", "3-O-ethyl ascorbic acid, resveratrol, vitamin E", "all", "29.50", "Stable antioxidant blend for daytime use."});
        products.add(new String[]{"Ultra C Spot Corrector", "Aurea Skin", "Treatment", "ascorbic acid 20%, kojic acid, alpha arbutin", "normal,oily,combination", "34.00", "Targets dark spots and uneven pigmentation."});
        products.add(new String[]{"C + Licorice Bright Emulsion", "NovaCare", "Emulsion", "magnesium ascorbyl phosphate, licorice root, niacinamide", "all", "27.30", "Brightening emulsion with low irritation profile."});

        products.add(new String[]{"Hydra Lock Hyaluronic Serum", "DermaLeaf", "Serum", "hyaluronic acid, sodium hyaluronate, panthenol", "all", "17.80", "Multi-weight hydration for daily plump skin."});
        products.add(new String[]{"Deep Moist HA Cream", "SkinMend", "Moisturizer", "hyaluronic acid, ceramides, squalane", "dry,normal,sensitive", "23.00", "Rich cream for dehydration and tightness."});
        products.add(new String[]{"HA Aqua Gel", "PureDerm", "Gel", "hyaluronic acid, betaine, trehalose", "oily,combination,normal", "18.40", "Cooling gel moisturizer for humid climates."});
        products.add(new String[]{"Hydro Plump Mist", "Clinesta", "Mist", "hyaluronic acid, glycerin, aloe vera", "all", "14.99", "Hydrating mist for on-the-go moisture."});
        products.add(new String[]{"Hyaluronic Night Mask", "Aurea Skin", "Mask", "hyaluronic acid, polyglutamic acid, allantoin", "all", "26.40", "Overnight mask to restore moisture reserves."});
        products.add(new String[]{"Barrier HA Repair Lotion", "NovaCare", "Lotion", "hyaluronic acid, ceramide NP, cholesterol", "dry,sensitive,normal", "24.20", "Barrier-focused lotion with long-lasting hydration."});

        products.add(new String[]{"Salicylic Clear Gel", "DermaLeaf", "Treatment", "salicylic acid 2%, niacinamide, zinc PCA", "oily,combination", "19.50", "Targets clogged pores and breakouts."});
        products.add(new String[]{"AHA Resurfacing Toner", "SkinMend", "Toner", "glycolic acid 7%, lactic acid 3%, allantoin", "normal,combination,oily", "21.90", "Gentle chemical exfoliation for texture."});
        products.add(new String[]{"BHA Spot Erase", "PureDerm", "Spot Treatment", "salicylic acid 2%, sulfur, tea tree oil", "oily,combination", "16.75", "Quick-response spot treatment."});
        products.add(new String[]{"Lactic Glow Peel", "Clinesta", "Peel", "lactic acid 10%, mandelic acid 5%, panthenol", "dry,normal,combination", "28.00", "Weekly peel for radiance and smoothness."});
        products.add(new String[]{"AHA/BHA Clarifying Cleanser", "Aurea Skin", "Cleanser", "glycolic acid, salicylic acid, glycerin", "oily,combination,normal", "20.30", "Low-foam exfoliating cleanser for congestion."});
        products.add(new String[]{"Multi-Acid Refining Serum", "NovaCare", "Serum", "glycolic acid, mandelic acid, salicylic acid, gluconolactone", "oily,combination", "32.10", "Advanced resurfacing serum for experienced users."});

        return products;
    }

    private void seedConflictRules(Connection connection) throws SQLException {
        String[][] rules = {
            {"retinol,AHA", "warning", "Retinol combined with AHA may cause severe irritation"},
            {"retinol,vitamin C", "caution", "May reduce effectiveness of both ingredients"},
            {"benzoyl peroxide,retinol", "warning", "Can cause excessive dryness and irritation"},
            {"salicylic acid,glycolic acid", "caution", "Over-exfoliation risk"},
            {"niacinamide,vitamin C", "advisory", "May reduce effectiveness slightly"},
            {"retinol,kojic acid", "caution", "Increased sensitivity risk"},
            {"AHA,physical exfoliant", "caution", "Over-exfoliation risk"},
            {"vitamin C,chemical SPF", "advisory", "May reduce SPF effectiveness"},
            {"benzoyl peroxide,hydroquinone", "warning", "Can cause skin staining"},
            {"multiple exfoliating acids", "warning", "Never combine multiple exfoliating acids"}
        };

        String existsSql = "SELECT COUNT(*) FROM conflict_rules WHERE interactingIngredients = ?";
        String insertSql = "INSERT INTO conflict_rules (ruleID, interactingIngredients, safetyLevel, adviceText, expertReviewed) VALUES (?, ?, ?, ?, ?)";

        for (String[] rule : rules) {
            boolean exists;
            try (PreparedStatement existsPs = connection.prepareStatement(existsSql)) {
                existsPs.setString(1, rule[0]);
                try (ResultSet rs = existsPs.executeQuery()) {
                    rs.next();
                    exists = rs.getInt(1) > 0;
                }
            }

            if (!exists) {
                try (PreparedStatement insertPs = connection.prepareStatement(insertSql)) {
                    insertPs.setString(1, UUID.randomUUID().toString());
                    insertPs.setString(2, rule[0]);
                    insertPs.setString(3, rule[1]);
                    insertPs.setString(4, rule[2]);
                    insertPs.setBoolean(5, true);
                    insertPs.executeUpdate();
                }
            }
        }
    }

    private void seedExpertAvailability(Connection connection, String expertId) throws SQLException {
        String existsSql = "SELECT COUNT(*) FROM expert_availability WHERE expertID=? AND availableDate=? AND timeSlot=?";
        String insertSql = "INSERT INTO expert_availability (availabilityID, expertID, availableDate, timeSlot, status) VALUES (?, ?, ?, ?, 'available')";

        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().plusDays(i + 1);
            for (String slot : AVAILABILITY_SLOTS) {
                boolean exists;
                try (PreparedStatement existsPs = connection.prepareStatement(existsSql)) {
                    existsPs.setString(1, expertId);
                    existsPs.setDate(2, Date.valueOf(date));
                    existsPs.setString(3, slot);
                    try (ResultSet rs = existsPs.executeQuery()) {
                        rs.next();
                        exists = rs.getInt(1) > 0;
                    }
                }

                if (!exists) {
                    try (PreparedStatement insertPs = connection.prepareStatement(insertSql)) {
                        insertPs.setString(1, UUID.randomUUID().toString());
                        insertPs.setString(2, expertId);
                        insertPs.setDate(3, Date.valueOf(date));
                        insertPs.setString(4, slot);
                        insertPs.executeUpdate();
                    }
                }
            }
        }
    }

    private void seedCompletedAppointments(Connection connection, String userId, String expertId) throws SQLException {
        LocalDate[] dates = {
            LocalDate.now().minusDays(14),
            LocalDate.now().minusDays(10),
            LocalDate.now().minusDays(6)
        };
        String[] slots = {"09:00", "11:00", "14:00"};
        String[] types = {"video", "chat", "in_person"};

        String existsSql = "SELECT COUNT(*) FROM appointments WHERE userID=? AND expertID=? AND appointmentDate=? AND timeSlot=?";
        String insertSql = "INSERT INTO appointments (appointmentID, userID, expertID, appointmentDate, timeSlot, type, fee, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'completed')";

        for (int i = 0; i < dates.length; i++) {
            boolean exists;
            try (PreparedStatement existsPs = connection.prepareStatement(existsSql)) {
                existsPs.setString(1, userId);
                existsPs.setString(2, expertId);
                existsPs.setDate(3, Date.valueOf(dates[i]));
                existsPs.setString(4, slots[i]);
                try (ResultSet rs = existsPs.executeQuery()) {
                    rs.next();
                    exists = rs.getInt(1) > 0;
                }
            }

            if (!exists) {
                try (PreparedStatement insertPs = connection.prepareStatement(insertSql)) {
                    insertPs.setString(1, UUID.randomUUID().toString());
                    insertPs.setString(2, userId);
                    insertPs.setString(3, expertId);
                    insertPs.setDate(4, Date.valueOf(dates[i]));
                    insertPs.setString(5, slots[i]);
                    insertPs.setString(6, types[i]);
                    insertPs.setDouble(7, 50.0);
                    insertPs.executeUpdate();
                }
            }
        }
    }
    public static void main(String[] args) {
    System.out.println("Starting database seeding...");
    new DatabaseSeeder().seedDatabase();
    System.out.println("Database seeding completed!");
}
}
