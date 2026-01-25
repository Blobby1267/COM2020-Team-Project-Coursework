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
    role TEXT CHECK(role IN ('participant', 'moderator')) NOT NULL,
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