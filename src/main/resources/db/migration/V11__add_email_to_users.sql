ALTER TABLE users
ADD COLUMN email VARCHAR(255);

UPDATE users
SET email = 'admin@florestal.com'
WHERE username = 'admin'
  AND email IS NULL;

UPDATE users
SET email = username || '@florestal.local'
WHERE email IS NULL;

ALTER TABLE users
ALTER COLUMN email SET NOT NULL;

ALTER TABLE users
ADD CONSTRAINT uk_users_email UNIQUE (email);
