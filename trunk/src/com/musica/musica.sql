DROP table IF EXISTS musica cascade;

CREATE TABLE musica(
id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
caminho varchar(500) NOT NULL UNIQUE ,
nome varchar(255) NOT NULL,
autor varchar(100),
genero varchar(30),
album varchar(100),
img varchar(500),
tempo INTEGER,
dt_mod_arquivo BIGINT);