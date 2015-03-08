--alter table musica add column perdida smallint
--UPDATE musica set perdida = 0
--SELECT *
--FROM musica
--WHERE
--perdida <> 1
--ORDER BY nome

ALTER TABLE playlist add column tipo smallint

SELECT *
FROM condicao
WHERE 1 = 1 
ORDER BY id



SELECT *
FROM condicao
WHERE 1 = 1 AND
playlist  = 0
ORDER BY id


SELECT *
FROM  musica
WHERE perdida = 0
AND tempo is null