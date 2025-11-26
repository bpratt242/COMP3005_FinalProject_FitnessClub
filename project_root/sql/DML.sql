INSERT INTO members(member_name, date_of_birth, gender, phone_number, email_address, target_weight) VALUES 
('Charlie Brown', '1950-10-02', 'M', '2426550000', 'charliebrown@peanuts.com', 75.0),
('Snoopy', '1950-10-04', 'M', '2426557657', 'joecool@peanuts.com', 40.0), 
('Woodstock', '1967-04-04', 'M', '2427980012', 'scoutwoodstock@peanuts.com', 15.0), 
('Peppermint Patty', '1966-08-22', 'F', '2424937043', 'patriciareichardt@peanuts.com', 80.0);

INSERT INTO health_metric(member_id, weight_kg, heart_rate, height, recorded_at) VALUES 
(1, 90.5, 72, 175.0, '2025-11-19'),
(2, 55.0, 68, 140.0, '2025-11-19'),
(3, 10.2, 54, 90.0, '2023-04-20'),
(4, 90.2, 65, 180.0, '2014-07-01');

INSERT INTO trainers(trainer_name, email_address, phone_number) VALUES
('Linus Van Pelt', 'security_blanket@peanuts.com', '2423758374'),
('Franklin Armstrong', 'busyFranklin@peanuts.com', '2424721039'),
('Lucy Van Pelt', 'schroedersgf@peanuts.com', '2423758377');

INSERT INTO room(room_name, capacity, room_location) VALUES 
('Flying Ace', 2, 'Third Floor'),
('Beethoven', 5, 'First Floor'),
('Great Pumpkin', 3, 'Third Floor');

INSERT INTO pt_session(member_id, trainer_id, room_id, start_time, end_time, session_status) VALUES
(1, 1, 3, 930, 1100, 'scheduled'),
(2, 3, 1, 1200, 1430, 'scheduled'),
(4, 2, 2, 1730, 1830, 'completed'),
(3, 3, 2, 930, 1030, 'completed');

INSERT INTO trainer_availability(trainer_id, start_time, end_time) VALUES 
(1, 1200, 1300),
(2, 930, 1100),
(3, 1500, 1700);