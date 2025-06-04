-- Create the user and grant priviledges. --
CREATE USER 'felfired'@'%' IDENTIFIED BY 'admin_root';
GRANT ALL ON *.* TO 'felfired'@'%' WITH GRANT OPTION;

-- Create the DVD database and switch to it. --
CREATE DATABASE dvd;
USE dvd;

-- Create the DVD information table. --
CREATE TABLE dvdlib
(
    uuid INT(15) NOT NULL PRIMARY KEY, 
    title VARCHAR(100) NOT NULL UNIQUE, 
    genre VARCHAR(50) NOT NULL, 
    amount int(15) NOT NULL
);

-- Create the user information table. --
CREATE TABLE staff
(
    username VARCHAR(20) NOT NULL, 
    password VARCHAR(20) NOT NULL
);

-- Inserting the values. --
INSERT INTO staff VALUES('employee','123');

-- Exiting. --
EXIT;