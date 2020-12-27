CREATE DATABASE chembase;
CREATE USER chembase_admin WITH PASSWORD 'admin';
GRANT ALL ON DATABASE chembase TO chembase_admin;