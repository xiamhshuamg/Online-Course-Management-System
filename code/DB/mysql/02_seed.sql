-- Optional seed data (execute in DataGrip)
-- IMPORTANT: Select the target schema in DataGrip before executing this script.
-- (We intentionally avoid hardcoding `USE <db>;` to reduce errors when you rename databases.)

-- Admin user:
-- The system stores BCrypt hashes in `users.password_hash`.
-- If you want to seed an admin via pure SQL, generate a BCrypt hash for "admin1234"
-- and replace <BCryptHashHere>.
--
-- Tip: If you have JDK set up, you can run:
--   cd back_end
--   ./mvnw -q -Dtest=BcryptHashGenTest test
-- to print a BCrypt hash.
USE `DB_design`;

INSERT INTO `users` (`created_at`, `updated_at`, `name`, `email`, `password_hash`, `role`, `status`)
VALUES (NOW(6), NOW(6), 'Administrator', 'admin@end.com', '<BCryptHashHere>', 'ADMIN', 'ACTIVE')
ON DUPLICATE KEY UPDATE
  `updated_at` = VALUES(`updated_at`);


