CREATE TABLE IF NOT EXISTS group_table (
    group_id INTEGER PRIMARY KEY AUTOINCREMENT,
    group_name TEXT NOT NULL,
    group_type TEXT CHECK(group_type IN ('hall', 'society', 'class')) NOT NULL
);

-- SELECT * FROM group_table;
-- SELECT name FROM sqlite_master WHERE type='table';

CREATE TABLE IF NOT EXISTS user_table (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    display_name TEXT NOT NULL,
    user_role TEXT CHECK(user_role IN ('participant', 'moderator')) NOT NULL,
    year_of_study TEXT CHECK(year_of_study IN ('1st Year', '2nd Year', '3rd Year', '4th Year', 'PostGraduate')) NOT NULL,
    campus TEXT CHECK(campus IN ('Streatham', 'St Lukes', 'Penryn', 'Truro')) NOT NULL,
    group_id INTEGER,
    FOREIGN KEY (group_id) REFERENCES group_table(group_id)
);

-- SELECT * FROM user_table;
-- SELECT name FROM sqlite_master WHERE type='table';

CREATE TABLE IF NOT EXISTS action_type (
    action_type_id INTEGER PRIMARY KEY AUTOINCREMENT,
    category TEXT NOT NULL,
    action_name TEXT NOT NULL,
    unit TEXT NOT NULL,
    co2e_per_unit REAL NOT NULL,
    notes TEXT
);

-- SELECT * FROM action_type;
-- SELECT name FROM sqlite_master WHERE type='table';

CREATE TABLE IF NOT EXISTS action_log (
    log_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    action_type_id INTEGER NOT NULL,
    quantity REAL NOT NULL,
    date TEXT NOT NULL,
    calculated_co2e REAL NOT NULL,
    evidence_required INTEGER CHECK(evidence_required IN (0,1)),
    FOREIGN KEY (user_id) REFERENCES user_table(user_id),
    FOREIGN KEY (action_type_id) REFERENCES action_type(action_type_id)
);

--SELECT * FROM action_log;
--SELECT name FROM sqlite_master WHERE type='table';

CREATE TABLE IF NOT EXISTS challenge (
    challenge_id INTEGER PRIMARY KEY AUTOINCREMENT,
    challenge_title TEXT NOT NULL,
    description TEXT,
    frequency TEXT CHECK(frequency IN('Daily', 'Weekly', 'Monthly')),
    start_date TEXT NOT NULL,
    end_date TEXT NOT NULL,
    scope TEXT CHECK(scope IN('Personal', 'Group')) NOT NULL
);

--SELECT * FROM challenge;
--SELECT name FROM sqlite_master WHERE type='table';

CREATE TABLE IF NOT EXISTS submission (
    submission_id INTEGER PRIMARY KEY AUTOINCREMENT,
    challenge_id INTEGER NOT NULL,
    user_id INTEGER,
    group_id INTEGER,
    status TEXT CHECK(status IN ('Pending', 'Approved', 'Rejected')) NOT NULL,
    total_co2e REAL NOT NULL,
    submitted_at TEXT NOT NULL,
    FOREIGN KEY (challenge_id) REFERENCES challenge(challenge_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (group_id) REFERENCES group_table(group_id)
);

--SELECT * FROM submission;
--SELECT name from sqlite_master WHERE type='table';

CREATE TABLE IF NOT EXISTS moderation_decision(
    decision_id INTEGER PRIMARY KEY AUTOINCREMENT,
    submission_id INTEGER NOT NULL,
    moderator_id INTEGER NOT NULL,
    decision TEXT CHECK(decision IN ('Approved', 'Rejected')) NOT NULL,
    reason TEXT,
    timestamp TEXT NOT NULL,
    FOREIGN KEY (submission_id) REFERENCES submission(submission_id),
    FOREIGN KEY (moderator_id) REFERENCES user_table(user_id)
);

--SELECT * FROM moderation_decision;
--SELECT name from sqlite_master WHERE type='table';

