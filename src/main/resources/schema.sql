CREATE TABLE sample_row_table(
    id bigint NOT NULL PRIMARY KEY,
    version bigint NOT NULL DEFAULT 0,
    value bigint NOT NULL
);