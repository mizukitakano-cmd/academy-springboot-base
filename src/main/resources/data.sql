TRUNCATE TABLE users RESTART IDENTITY CASCADE;

INSERT INTO users (name, email, password)
VALUES ('kinako', 'kinako@mail.com', '$2a$10$0p2P6Ushm087W7xQ3A25Mep2fscbV2Xq.pAen1090huasw7Z6tGOW');

