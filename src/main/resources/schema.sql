-- Table for storing user information
CREATE TABLE IF NOT EXISTS userinfo (
                                        id SERIAL PRIMARY KEY,
                                        email VARCHAR(50) NOT NULL UNIQUE,
                                        first_name VARCHAR(30) NOT NULL,
                                        last_name VARCHAR(30),
                                        password VARCHAR(255) NOT NULL,
                                        role VARCHAR(20) NOT NULL,
                                        active BOOLEAN NOT NULL DEFAULT true,
                                        manager_id INTEGER REFERENCES userinfo(id), -- Self-referencing foreign key
                                        onboarding_date DATE NOT NULL
);

-- Table for storing leave types
CREATE TABLE IF NOT EXISTS leave_types (
                                           id SERIAL PRIMARY KEY,
                                           type_name VARCHAR(50) UNIQUE NOT NULL,
                                           description TEXT,
                                           annual_allocation INTEGER,
                                           max_accumulation INTEGER
);

-- Table for storing leave details
CREATE TABLE IF NOT EXISTS leave_details (
                                             id SERIAL PRIMARY KEY,
                                             user_id INTEGER NOT NULL REFERENCES userinfo(id),
                                             employee_name VARCHAR(50) NOT NULL,
                                             from_date DATE NOT NULL,
                                             to_date DATE NOT NULL,
                                             leave_type_id INTEGER NOT NULL REFERENCES leave_types(id),
                                             reason VARCHAR(300) NOT NULL,
                                             duration INTEGER,
                                             status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                             active BOOLEAN DEFAULT true
);

-- Table for tracking user leave balances
CREATE TABLE IF NOT EXISTS user_leave_balance (
                                                   user_id INTEGER NOT NULL REFERENCES userinfo(id),
                                                  leave_type_id INTEGER NOT NULL REFERENCES leave_types(id),
                                                  current_balance INTEGER,
                                                  year INTEGER NOT NULL,
                                                  PRIMARY KEY (user_id, leave_type_id, year)
);
