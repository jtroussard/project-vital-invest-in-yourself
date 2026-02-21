# Project Vital: Invest in Yourself

## Database

### Local setup

```sql
-- 1. Create role
CREATE ROLE "user" WITH LOGIN PASSWORD 'password';

-- 2. Create database
CREATE DATABASE projectvital OWNER "user";

-- 3. Grant privileges (safe even if owner already)
GRANT ALL PRIVILEGES ON DATABASE projectvital TO "user";

-- 4. Connect to your specific database first
\c projectvital

-- 5. Grant permissions to your app user
GRANT ALL ON SCHEMA public TO "user";
```
