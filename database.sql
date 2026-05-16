CREATE DATABASE IF NOT EXISTS smartskin;
USE smartskin;

CREATE TABLE users (
    userID VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    age INT,
    gender VARCHAR(10),
    role ENUM('end_user','expert','admin') NOT NULL,
    specialization VARCHAR(100),
    consultationFee DOUBLE,
    rating DOUBLE DEFAULT 0,
    isVerified BOOLEAN DEFAULT FALSE,
    isActive BOOLEAN DEFAULT TRUE,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE skin_profiles (
    profileID VARCHAR(36) PRIMARY KEY,
    userID VARCHAR(36) NOT NULL,
    skinType ENUM('oily','dry','combination',
                  'sensitive','normal') NOT NULL,
    skinConcerns TEXT,
    knownAllergies TEXT,
    currentProducts TEXT,
    diet VARCHAR(50),
    sleepHours INT,
    stressLevel ENUM('low','medium','high'),
    sunExposure VARCHAR(50),
    completenessPercentage INT DEFAULT 0,
    updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userID) REFERENCES users(userID)
        ON DELETE CASCADE
);

CREATE TABLE products (
    productID VARCHAR(36) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    brand VARCHAR(100),
    category VARCHAR(100),
    ingredients TEXT,
    suitableSkinTypes TEXT,
    price DOUBLE,
    description TEXT,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE skincare_routines (
    routineID VARCHAR(36) PRIMARY KEY,
    userID VARCHAR(36) NOT NULL,
    routineType ENUM('morning','evening') NOT NULL,
    steps TEXT,
    generatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    isExpertValidated BOOLEAN DEFAULT FALSE,
    expertID VARCHAR(36),
    FOREIGN KEY (userID) REFERENCES users(userID)
        ON DELETE CASCADE
);

CREATE TABLE appointments (
    appointmentID VARCHAR(36) PRIMARY KEY,
    userID VARCHAR(36) NOT NULL,
    expertID VARCHAR(36) NOT NULL,
    appointmentDate DATE NOT NULL,
    timeSlot VARCHAR(20) NOT NULL,
    type ENUM('video','in_person','chat') NOT NULL,
    fee DOUBLE,
    status ENUM('pending','confirmed','cancelled',
                'completed','no_show') DEFAULT 'pending',
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userID) REFERENCES users(userID)
        ON DELETE CASCADE,
    FOREIGN KEY (expertID) REFERENCES users(userID)
        ON DELETE CASCADE
);

CREATE TABLE progress_entries (
    entryID VARCHAR(36) PRIMARY KEY,
    userID VARCHAR(36) NOT NULL,
    entryDate DATE NOT NULL,
    acneLevel INT CHECK (acneLevel BETWEEN 1 AND 5),
    dryness INT CHECK (dryness BETWEEN 1 AND 5),
    pigmentation INT CHECK (pigmentation BETWEEN 1 AND 5),
    irritation INT CHECK (irritation BETWEEN 1 AND 5),
    photoPath VARCHAR(500),
    notes TEXT,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userID) REFERENCES users(userID)
        ON DELETE CASCADE
);

CREATE TABLE conflict_rules (
    ruleID VARCHAR(36) PRIMARY KEY,
    interactingIngredients TEXT NOT NULL,
    safetyLevel ENUM('advisory','caution','warning') NOT NULL,
    adviceText TEXT NOT NULL,
    expertReviewed BOOLEAN DEFAULT FALSE,
    lastUpdated DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reviews (
    reviewID VARCHAR(36) PRIMARY KEY,
    userID VARCHAR(36) NOT NULL,
    expertID VARCHAR(36) NOT NULL,
    appointmentID VARCHAR(36) NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userID) REFERENCES users(userID)
        ON DELETE CASCADE,
    FOREIGN KEY (expertID) REFERENCES users(userID)
        ON DELETE CASCADE,
    FOREIGN KEY (appointmentID)
        REFERENCES appointments(appointmentID)
        ON DELETE CASCADE
);

CREATE TABLE favorites (
    userID VARCHAR(36) NOT NULL,
    productID VARCHAR(36) NOT NULL,
    savedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (userID, productID),
    FOREIGN KEY (userID) REFERENCES users(userID)
        ON DELETE CASCADE,
    FOREIGN KEY (productID) REFERENCES products(productID)
        ON DELETE CASCADE
);

CREATE TABLE notifications (
    notifID VARCHAR(36) PRIMARY KEY,
    userID VARCHAR(36) NOT NULL,
    title VARCHAR(200) NOT NULL,
    body TEXT NOT NULL,
    isRead BOOLEAN DEFAULT FALSE,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userID) REFERENCES users(userID)
        ON DELETE CASCADE
);

CREATE TABLE audit_logs (
    logID VARCHAR(36) PRIMARY KEY,
    actorID VARCHAR(36) NOT NULL,
    action VARCHAR(200) NOT NULL,
    targetID VARCHAR(36),
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE validation_queue (
    itemID VARCHAR(36) PRIMARY KEY,
    routineID VARCHAR(36) NOT NULL,
    userID VARCHAR(36) NOT NULL,
    status ENUM('pending','approved',
                'modified','rejected') DEFAULT 'pending',
    submittedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    reviewedBy VARCHAR(36),
    reviewedAt DATETIME,
    rejectionReason TEXT,
    FOREIGN KEY (routineID)
        REFERENCES skincare_routines(routineID)
        ON DELETE CASCADE
);

CREATE TABLE expert_availability (
    availabilityID VARCHAR(36) PRIMARY KEY,
    expertID VARCHAR(36) NOT NULL,
    availableDate DATE NOT NULL,
    timeSlot VARCHAR(20) NOT NULL,
    status ENUM('available','booked','held')
           DEFAULT 'available',
    FOREIGN KEY (expertID) REFERENCES users(userID)
        ON DELETE CASCADE
);

-- INDEXES for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_skinprofile_user
    ON skin_profiles(userID);
CREATE INDEX idx_appointments_user
    ON appointments(userID);
CREATE INDEX idx_appointments_expert
    ON appointments(expertID);
CREATE INDEX idx_progress_user
    ON progress_entries(userID);
CREATE INDEX idx_notifications_user
    ON notifications(userID);
