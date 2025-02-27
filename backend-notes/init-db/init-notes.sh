#!/bin/sh
set -e

mongosh admin <<-EOMONGO
  db = db.getSiblingDB('$MONGO_INITDB_DATABASE');
  db.createUser({
    user: '$MONGO_INITDB_ROOT_USERNAME',
    pwd: '$MONGO_INITDB_ROOT_PASSWORD',
    roles: [{ role: 'readWrite', db: '$MONGO_INITDB_DATABASE' }]
  });
EOMONGO

#	// Sample data for easy try with docker
#	db.note.insertMany([
#		{
#			"patId": 1,
#			"patient": "TestNone",
#			"content": "Le patient déclare qu’il « se sent très bien ». Poids égal ou inférieur au poids recommandé."
#		},
#		{
#			"patId": 2,
#			"patient": "TestBorderline",
#			"content": "Le patient déclare qu’il ressent beaucoup de stress au travail. Il se plaint également que son audition est anormale dernièrement."
#		},
#		{
#			"patId": 2,
#			"patient": "TestBorderline",
#			"content": "Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois. Il remarque également que son audition continue d’être anormale."
#		},
#		{
#			"patId": 3,
#			"patient": "TestInDanger",
#			"content": "Le patient déclare qu’il fume depuis peu."
#		},
#		{
#			"patId": 3,
#			"patient": "TestInDanger",
#			"content": "Le patient déclare qu’il est fumeur et qu’il a cessé de fumer l’année dernière. Il se plaint également de crises d’apnée anormales. Tests de laboratoire indiquant un taux de cholestérol LDL élevé."
#		},
#		{
#			"patId": 4,
#			"patient": "TestEarlyOnset",
#			"content": "Le patient déclare qu’il lui est devenu difficile de monter les escaliers. Il se plaint également d’être essoufflé. Tests de laboratoire indiquant que les anticorps sont élevés. Réaction aux médicaments."
#		},
#		{
#			"patId": 4,
#			"patient": "TestEarlyOnset",
#			"content": "Le patient déclare qu’il a mal au dos lorsqu’il reste assis longtemps."
#		},
#		{
#			"patId": 4,
#			"patient": "TestEarlyOnset",
#			"content": "Le patient déclare avoir commencé à fumer depuis peu. Hémoglobine A1C supérieure au niveau recommandé."
#		},
#		{
#			"patId": 4,
#			"patient": "TestEarlyOnset",
#			"content": "Taille, Poids, Cholestérol, Vertige et Réaction"
#		},
#	])