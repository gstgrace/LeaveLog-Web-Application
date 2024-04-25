-- Inserting the CEO who does not have a manager
INSERT INTO userinfo (email, first_name, last_name, password, role, active, manager_id, onboarding_date) VALUES
    ('ceo@email.com', 'Big', 'Boss', '$2a$10$LDYENK7VNEKyJXk8xV3YPOHnBJzVSSvTCVKri7EgpAebqXMz7Xxme', 'Manager', true, NULL, '2024-01-01')
ON CONFLICT (email) DO NOTHING;

-- Inserting 5 managers, each managed by the CEO
INSERT INTO userinfo (email, first_name, last_name, password, role, active, manager_id, onboarding_date) VALUES
                                                                                                             ('manager1@email.com', 'Manager', 'One', '$2a$10$yDUmZLXfTyC0svML3cHIJuqP4VNJycCfyu3zdBt0ysbqaj7DtpNIS', 'Manager', true, (SELECT id FROM userinfo WHERE email = 'ceo@email.com'), '2024-02-01'),
                                                                                                             ('manager2@email.com', 'Manager', 'Two', '$2a$10$qrjd9xGhu3D4R3P5mmxT2eCYhb4d2B9LaHSyWN2SOYy.BEFTztknS', 'Manager', true, (SELECT id FROM userinfo WHERE email = 'ceo@email.com'), '2024-02-15'),
                                                                                                             ('manager3@email.com', 'Manager', 'Three', '$2a$10$vtGp5uLt/leXIjYCh1oFcevU5lkUrlG0Tk8VVPIqPna0kJWE57TJu', 'Manager', true, (SELECT id FROM userinfo WHERE email = 'ceo@email.com'), '2024-03-01'),
                                                                                                             ('manager4@email.com', 'Manager', 'Four', '$2a$10$cTWHi9UoTSgi7Up1SpxrheXX8YVYsAUuL6lua3fM7LilrxVcDpvCG', 'Manager', true, (SELECT id FROM userinfo WHERE email = 'ceo@email.com'), '2024-03-15'),
                                                                                                             ('manager5@email.com', 'Manager', 'Five', '$2a$10$lnpitDrJ5DUJGYNp9HMGRuW1UbwJYEwiSRKlhsS.lK6UV2pindO9S', 'Manager', true, (SELECT id FROM userinfo WHERE email = 'ceo@email.com'), '2024-04-01')
ON CONFLICT (email) DO NOTHING;

-- Inserting various employees and lower-level managers, each managed by different managers
INSERT INTO userinfo (email, first_name, last_name, password, role, active, manager_id, onboarding_date) VALUES
                                                                                                             ('employee1.1@email.com', 'Employee', 'OneOne', '$2a$10$Y98bi7yK2rPrDm4IkknOveQxkHHCX7SuVFT8HbaOb0Wt4iSF2ATly', 'Employee', true, (SELECT id FROM userinfo WHERE email = 'manager1@email.com'), '2024-04-10'),
                                                                                                             ('employee1.2@email.com', 'Employee', 'OneTwo', '$2a$10$x4PG92FrrpRhPVdWAVeO0.LtKnWWqpVNPeqNv6bL1qbhCFtc.xJPS', 'Employee', true, (SELECT id FROM userinfo WHERE email = 'manager1@email.com'), '2024-04-20'),
                                                                                                             ('employee2.1@email.com', 'Employee', 'TwoOne', '$2a$10$aeUhUPMD7W5yB7jukBRxzOdA/fVYFuBMaNeZM3jKc1rK5wEKWQReq', 'Employee', true, (SELECT id FROM userinfo WHERE email = 'manager2@email.com'), '2024-05-01'),
                                                                                                             ('employee2.2@email.com', 'Employee', 'TwoTwo', '$2a$10$CmnZNth.ek0T6ihhLWlyPuRssNEbG.aOzUV.r89gvXP1/1ftQRD5a', 'Employee', true, (SELECT id FROM userinfo WHERE email = 'manager2@email.com'), '2024-05-15'),
                                                                                                             ('employee3.1@email.com', 'Employee', 'ThreeOne', '$2a$10$AL1AwMy89PHopuzo5C637uqA3E9ByzUBy3TDurJG0ZtQgtQ/Q5mPy', 'Employee', true, (SELECT id FROM userinfo WHERE email = 'manager3@email.com'), '2024-06-01'),
                                                                                                             ('manager3.1@email.com', 'Manager', 'ThreeOne', '$2a$10$g9a.jea5cmJkvJToxaHcBeU2ysACPIwpINAXopuD7bd0cx6n2uvVq', 'Manager', true, (SELECT id FROM userinfo WHERE email = 'manager3@email.com'), '2024-06-10'),
                                                                                                             ('manager4.1@email.com', 'Manager', 'FourOne', '$2a$10$HWH64OXN0I26./TScLR/luwsG00/OB5.1FW.60W3TdAYLYekVRjTK', 'Manager', true, (SELECT id FROM userinfo WHERE email = 'manager4@email.com'), '2024-07-15'),
                                                                                                             ('employee5.1@email.com', 'Employee', 'FiveOne', '$2a$10$KAtDHNf9NIPLvNCVJvM7E.GfUSG3Cq/UiLHZWKTHgBO0ISMgP9tzW', 'Employee', true, (SELECT id FROM userinfo WHERE email = 'manager5@email.com'), '2024-08-01')
ON CONFLICT (email) DO NOTHING;

-- Inserting lower-level employees
INSERT INTO userinfo (email, first_name, last_name, password, role, active, manager_id, onboarding_date) VALUES
                                                                                                             ('employee3.1.1@email.com', 'Employee', 'ThreeOneOne', '$2a$10$YEHixMtWFaex64gwDh724eYM/aiyIdtwqu6BMQ809HQ3S3eUK5Hlm', 'Employee', true, (SELECT id FROM userinfo WHERE email = 'manager3.1@email.com'), '2024-07-01'),
                                                                                                             ('employee4.1.1@email.com', 'Employee', 'FourOneOne', '$2a$10$YEHixMtWFaex64gwDh724eYM/aiyIdtwqu6BMQ809HQ3S3eUK5Hlm', 'Employee', true, (SELECT id FROM userinfo WHERE email = 'manager4.1@email.com'), '2024-09-01')
ON CONFLICT (email) DO NOTHING;

-- Insert predefined leave types
INSERT INTO leave_types (type_name, description, annual_allocation, max_accumulation) VALUES
                                                                                          ('Vacation Leave', '20 days per year, max out at 30 days.', 20, 30),
                                                                                          ('Sick Leave', '10 days per year, max out at 20 days.', 10, 20),
                                                                                          ('Company Holiday', 'Company designated holidays, no need to apply.', NULL, NULL),
                                                                                          ('Volunteer Day Off', '1 day per year, no carry over.', 1, 1),
                                                                                          ('Jury Duty Leave', '10 days, no carry over.', 10, 10),
                                                                                          ('Bereavement Leave', '10 days, no carry over.', 10, 10),
                                                                                          ('FMLA/CFRA', '12 weeks per year, no carry over.', 72, 72),
                                                                                          ('Paid Parental Leave', '12 weeks, no carry over.', 72, 72),
                                                                                          ('Personal Leave of Absence', 'Apply as needed, no pre-load.', NULL, NULL)
ON CONFLICT (type_name) DO NOTHING;

-- Initialization script for user leave balances with conditions for Vacation Leave
INSERT INTO user_leave_balance (user_id, leave_type_id, current_balance, year)
SELECT u.id,
       lt.id,
       CASE
           -- Handle Vacation Leave with prorated values based on the quarter of onboarding
           WHEN lt.type_name = 'Vacation Leave' THEN
               CASE
                   WHEN EXTRACT(QUARTER FROM u.onboarding_date) = 1 THEN ROUND(lt.annual_allocation * 0.75)
                   WHEN EXTRACT(QUARTER FROM u.onboarding_date) = 2 THEN ROUND(lt.annual_allocation * 0.5)
                   WHEN EXTRACT(QUARTER FROM u.onboarding_date) = 3 THEN ROUND(lt.annual_allocation * 0.25)
                   ELSE 0
                   END
           -- Allocate full annual allocation for other leave types
           ELSE lt.annual_allocation
           END AS initial_balance,
       EXTRACT(YEAR FROM CURRENT_DATE) AS year
FROM userinfo u
         JOIN leave_types lt ON lt.annual_allocation IS NOT NULL
WHERE
    -- Ensure to only set up balances for the current year or handle multiple years based on the employee's onboarding date
    EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM u.onboarding_date) <= 1
ON CONFLICT (user_id, leave_type_id, year) DO NOTHING;

