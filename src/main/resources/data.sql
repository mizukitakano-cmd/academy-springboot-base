DELETE FROM users;

INSERT INTO users (name, email, password)
SELECT 'kinako', 'kinako@mail.com' '$2a$10$0p2P6Ushm087W7xQ3A25Mep2fscbV2Xq.pAen1090huasw7Z6tGOW'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'kinako@mail.com'
);
