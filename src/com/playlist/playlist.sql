DROP table IF EXISTS playlist cascade;

CREATE TABLE playlist(
id SMALLINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
nome varchar(30) NOT NULL UNIQUE,
nrMus SMALLINT);