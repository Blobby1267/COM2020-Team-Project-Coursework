CREATE TABLE IF NOT EXISTS group_table (
    group_id INTEGER PRIMARY KEY AUTOINCREMENT,
    group_name TEXT NOT NULL,
    group_type TEXT CHECK(group_type IN ('hall', 'society', 'class')) NOT NULL
);

-- SELECT * FROM group_table;

-- SELECT name FROM sqlite_master WHERE type='table';
