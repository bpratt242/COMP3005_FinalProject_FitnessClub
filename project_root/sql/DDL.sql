CREATE TABLE members(
    member_id          SERIAL PRIMARY KEY,
    member_name        VARCHAR(100) NOT NULL,
    date_of_birth      DATE,
    gender             VARCHAR(10),
    phone_number       VARCHAR(10),
    email_address      VARCHAR(50) UNIQUE,
    target_weight      NUMERIC(5,2)           

);

CREATE TABLE health_metric(
    metric_id           SERIAL PRIMARY KEY,
    member_id           INTEGER NOT NULL REFERENCES members(member_id),
    weight_kg           NUMERIC(5,2),
    heart_rate          INTEGER,
    height              NUMERIC(5,2),
    recorded_at         DATE NOT NULL

);

CREATE TABLE trainers(
    trainer_id          SERIAL PRIMARY KEY,
    trainer_name        VARCHAR(100) NOT NULL,
    email_address       VARCHAR(50) UNIQUE,
    phone_number        VARCHAR(20)
);

CREATE TABLE room(
    room_id             SERIAL PRIMARY KEY,
    room_name           VARCHAR(20) NOT NULL,
    capacity            INTEGER NOT NULL CHECK(capacity > 0),
    room_location       VARCHAR(20)

);

CREATE TABLE pt_session(
    session_id          SERIAL PRIMARY KEY,
    member_id           INTEGER NOT NULL REFERENCES members(member_id),
    trainer_id          INTEGER NOT NULL REFERENCES trainers(trainer_id),
    room_id             INTEGER NOT NULL REFERENCES room(room_id),
    start_time          INTEGER NOT NULL,
    end_time            INTEGER NOT NULL,
    session_status      VARCHAR(10) NOT NULL
        CHECK(session_status IN ('scheduled', 'completed', 'cancelled')),
    CHECK(start_time < end_time)
);

CREATE TABLE trainer_availability(
    availability_id     SERIAL PRIMARY KEY,
    trainer_id          INTEGER NOT NULL REFERENCES trainers(trainer_id),
    start_time          INTEGER NOT NULL,
    end_time            INTEGER NOT NULL,
    CHECK(start_time < end_time)

);

CREATE VIEW trainer_schedule AS 
    SELECT
        ps.session_id,
        ps.trainer_id,
        ps.member_id,
        ps.start_time,
        ps.end_time,
        ps.session_status,
        m.member_name,
        r.room_name,
        t.trainer_name
    FROM pt_session ps
    JOIN trainers t ON ps.trainer_id = t.trainer_id
    JOIN members m ON ps.member_id = m.member_id
    JOIN room r ON ps.room_id = r.room_id;

CREATE INDEX ptsession_roomtime_index
ON pt_session(room_id, start_time, end_time);

CREATE FUNCTION check_no_overlap_trainer_availability()
    RETURNS TRIGGER
    LANGUAGE plpgsql
    AS
$$  
BEGIN
    IF EXISTS(
        SELECT 1
        FROM trainer_availability ta 
        WHERE ta.trainer_id = NEW.trainer_id
            AND NOT(ta.end_time <= NEW.start_time OR ta.start_time >= NEW.end_time)
    ) 
    THEN
        RETURN NULL;
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER check_no_overlap_trainer_availability
    BEFORE INSERT ON trainer_availability
    FOR EACH ROW
    EXECUTE PROCEDURE check_no_overlap_trainer_availability();
