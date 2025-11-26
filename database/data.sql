-- ========================================
-- สคริปต์จำลองข้อมูลอัตโนมัติสำหรับ Spring Boot
-- ========================================
-- ไฟล์นี้จะถูกรันอัตโนมัติทุกครั้งที่ Start Application
-- ========================================

-- ========================================
-- 1. เพิ่มข้อมูล SkillType
-- ========================================
INSERT IGNORE INTO skill_type (skillTypeId, skill_type_name, skill_type_detail) VALUES
(1, 'General Cleaning', 'ทำความสะอาดทั่วไป'),
(2, 'Laundry', 'ซักผ้า'),
(3, 'Cooking', 'ทำอาหาร'),
(4, 'Garden', 'ดูแลสวน');

-- ========================================
-- 2. เพิ่มข้อมูล SkillLevelTier
-- ========================================
INSERT IGNORE INTO skill_level_tier (id, skill_level_name, min_hires_for_level) VALUES
(1, 'Beginner', 0),
(2, 'Intermediate', 5),
(3, 'Advanced', 15),
(4, 'Expert', 30);

-- ========================================
-- 3. เพิ่มข้อมูล Login
-- ========================================
-- รหัสผ่านทั้งหมดคือ "password123"
INSERT IGNORE INTO login (username, password) VALUES
('admin001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('housekeeper001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('housekeeper002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('housekeeper003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('hirer001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('hirer002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('hirer003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- ========================================
-- 4. เพิ่มข้อมูล Person
-- ========================================
INSERT IGNORE INTO person (person_id, email, firstName, lastName, idCardNumber, phoneNumber, address, picture_url, accountStatus, login_username) VALUES
(1, 'admin@maebanjumpen.com', 'Admin', 'System', '1234567890123', '0812345678', '123 Admin St., Bangkok', NULL, 'ACTIVE', 'admin001'),
(2, 'somchai@email.com', 'สมชาย', 'ใจดี', '1234567890124', '0823456789', '456 Housekeeper Rd., Bangkok', NULL, 'ACTIVE', 'housekeeper001'),
(3, 'malee@email.com', 'มาลี', 'สวยงาม', '1234567890125', '0834567890', '789 Housekeeper Ave., Bangkok', NULL, 'ACTIVE', 'housekeeper002'),
(4, 'somsak@email.com', 'สมศักดิ์', 'ขยัน', '1234567890126', '0845678901', '321 Housekeeper Ln., Bangkok', NULL, 'ACTIVE', 'housekeeper003'),
(5, 'nida@email.com', 'นิดา', 'รักบ้าน', '1234567890127', '0856789012', '654 Customer St., Bangkok', NULL, 'ACTIVE', 'hirer001'),
(6, 'preecha@email.com', 'ปรีชา', 'มั่งมี', '1234567890128', '0867890123', '987 Customer Rd., Bangkok', NULL, 'ACTIVE', 'hirer002'),
(7, 'suda@email.com', 'สุดา', 'สะอาด', '1234567890129', '0878901234', '147 Customer Ave., Bangkok', NULL, 'ACTIVE', 'hirer003');

-- ========================================
-- 5. เพิ่มข้อมูล PartyRole (Admin)
-- ========================================
INSERT IGNORE INTO PartyRole (id, DTYPE, person_id, admin_status) VALUES
(1, 'admin', 1, 'ACTIVE');

-- ========================================
-- 6. เพิ่มข้อมูล PartyRole (Housekeeper)
-- ========================================
INSERT IGNORE INTO PartyRole (id, DTYPE, person_id, balance, photo_verify_url, status_verify, rating, daily_rate) VALUES
(2, 'housekeeper', 2, 5000.00, NULL, 'APPROVED', 4.5, '800'),
(3, 'housekeeper', 3, 3000.00, NULL, 'APPROVED', 4.8, '900'),
(4, 'housekeeper', 4, 2000.00, NULL, 'PENDING', 0.0, '750');

-- ========================================
-- 7. เพิ่มข้อมูล PartyRole (Hirer)
-- ========================================
INSERT IGNORE INTO PartyRole (id, DTYPE, person_id, balance) VALUES
(5, 'hirer', 5, 10000.00),
(6, 'hirer', 6, 15000.00),
(7, 'hirer', 7, 8000.00);

-- ========================================
-- 8. เพิ่มข้อมูล HousekeeperSkill
-- ========================================
INSERT IGNORE INTO housekeeper_skill (skillId, housekeeper_id, skill_type_id, skill_level_tier_id, total_hires_completed, price_per_day) VALUES
(1, 2, 1, 2, 0, 800.00),
(2, 2, 2, 1, 0, 600.00),
(3, 3, 1, 3, 0, 900.00),
(4, 3, 3, 2, 0, 850.00),
(5, 3, 4, 2, 0, 800.00),
(6, 4, 1, 1, 0, 750.00);

-- ========================================
-- 9. เพิ่มข้อมูล Hire
-- ========================================
INSERT IGNORE INTO hire (hireId, hireName, hireDetail, paymentAmount, hireDate, startDate, startTime, endTime, location, jobStatus, hirer_id, housekeeper_id, skill_type_id) VALUES
(1, 'ทำความสะอาดบ้าน', 'ทำความสะอาดบ้าน 2 ชั้น พร้อมเช็ดกระจก', 1600.00, '2024-01-15 10:00:00', '2024-01-20', '09:00:00', '13:00:00', '123/45 ถนนสุขุมวิท กรุงเทพฯ', 'COMPLETED', 5, 2, 1),
(2, 'ซักผ้าและรีดผ้า', 'ซักผ้าและรีดผ้า 20 ชิ้น', 800.00, '2024-01-18 14:00:00', '2024-01-22', '10:00:00', '14:00:00', '456/78 ถนนพระราม 4 กรุงเทพฯ', 'COMPLETED', 6, 2, 2),
(3, 'ทำอาหารมื้อเย็น', 'ทำอาหารไทย 5 เมนู สำหรับ 10 คน', 2700.00, '2024-01-20 09:00:00', '2024-01-25', '15:00:00', '18:00:00', '789/12 ถนนสาทร กรุงเทพฯ', 'COMPLETED', 7, 3, 3),
(4, 'ดูแลสวน', 'ตัดหญ้า รดน้ำต้นไม้ และตกแต่งสวน', 1800.00, '2024-02-01 08:00:00', '2024-02-05', '08:00:00', '12:00:00', '321/56 ถนนพหลโยธิน กรุงเทพฯ', 'IN_PROGRESS', 5, 3, 4),
(5, 'ทำความสะอาดคอนโด', 'ทำความสะอาดคอนโด 1 ห้องนอน', 1200.00, '2024-02-10 11:00:00', '2024-02-15', '10:00:00', '14:00:00', '654/89 ถนนเพชรบุรี กรุงเทพฯ', 'PENDING', 6, 4, 1);

-- ========================================
-- 10. เพิ่มข้อมูล Review
-- ========================================
INSERT IGNORE INTO review (reviewId, review_message, score, review_date, hire_id) VALUES
(1, 'ทำงานดีมาก สะอาดเกินคาด แนะนำเลยค่ะ', 5.0, '2024-01-20 14:00:00', 1),
(2, 'ซักผ้าสะอาด รีดเรียบร้อย ประทับใจค่ะ', 4.5, '2024-01-22 15:00:00', 2),
(3, 'อาหารอร่อยมาก ทุกคนชอบ จะจ้างอีกแน่นอน', 5.0, '2024-01-25 19:00:00', 3);

-- ========================================
-- 11. เพิ่มข้อมูล Transaction
-- ========================================
INSERT IGNORE INTO transactions (transactionId, transaction_type, transactionAmount, transaction_date, transaction_status, member_id, prompay_number, bank_account_number, bank_account_name, transaction_approval_date) VALUES
(1, 'DEPOSIT', 10000.00, '2024-01-10 10:00:00', 'APPROVED', 5, NULL, NULL, NULL, '2024-01-10 10:05:00'),
(2, 'DEPOSIT', 15000.00, '2024-01-12 11:00:00', 'APPROVED', 6, NULL, NULL, NULL, '2024-01-12 11:05:00'),
(3, 'DEPOSIT', 8000.00, '2024-01-14 09:00:00', 'APPROVED', 7, NULL, NULL, NULL, '2024-01-14 09:05:00'),
(4, 'WITHDRAW', 2000.00, '2024-01-25 14:00:00', 'APPROVED', 2, '0823456789', NULL, 'สมชาย ใจดี', '2024-01-25 15:00:00'),
(5, 'WITHDRAW', 1500.00, '2024-01-26 10:00:00', 'PENDING', 3, NULL, '1234567890', 'มาลี สวยงาม', NULL);

-- ========================================
-- 12. เพิ่มข้อมูล Penalty
-- ========================================
INSERT IGNORE INTO penalty (penaltyId, penaltyType, penaltyDetail, penaltyDate, penaltyStatus) VALUES
(1, 'WARNING', 'มาสายเกิน 30 นาที', '2024-02-01 10:00:00', 'ACTIVE');

-- ========================================
-- 13. เพิ่มข้อมูล Report
-- ========================================
INSERT IGNORE INTO report (reportId, reportTitle, reportMessage, reportDate, reportStatus, reporter_id, hire_id, penalty_id) VALUES
(1, 'แจ้งปัญหาการมาสาย', 'แม่บ้านมาสายเกิน 30 นาที ทำให้ต้องรอนาน', '2024-02-01 09:30:00', 'RESOLVED', 5, 4, 1);

