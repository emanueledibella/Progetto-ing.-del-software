CREATE DATABASE DatabaseAzienda;
USE DatabaseAzienda;

CREATE TABLE Farmacia
(
	idFarmacia INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	nome CHAR(20) NOT NULL,
	numeroTelefono CHAR(13) NOT NULL,
	indirizzo CHAR(45) NOT NULL
);
INSERT INTO Farmacia(nome, numeroTelefono, indirizzo) VALUES('PharmaOne', '0918428472', 'Via Dante 44');
INSERT INTO Farmacia(nome, numeroTelefono, indirizzo) VALUES('PharmaTwo', '0912740196', 'Via Roma 16');
INSERT INTO Farmacia(nome, numeroTelefono, indirizzo) VALUES('PharmaThree', '0915381921', 'Corso Vittorio Emanuele 12');

CREATE TABLE Corriere
(
	idUtente INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	email CHAR(35) NOT NULL,
	hashPassword CHAR(40) NOT NULL
);
INSERT INTO Corriere(email, hashPassword) VALUES('corriere1@azienda.it', 'bf637120cf6428c9581fa310b978c9a1');	-- PASSWORD: corriere1
INSERT INTO Corriere(email, hashPassword) VALUES('corriere2@azienda.it', 'bfeaed266e3c2bf3879afd915b295b6a');	-- PASSWORD: corriere2
INSERT INTO Corriere(email, hashPassword) VALUES('corriere3@azienda.it', '2f56f9fa8e84748af27ea1eca38a5f5e');	-- PASSWORD: corriere3


CREATE TABLE AddettoAzienda
(
	idUtente INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	email CHAR(35) NOT NULL,
	hashPassword CHAR(40) NOT NULL
);

CREATE TABLE Farmaco
(
	idFarmaco INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	nome CHAR(30) NOT NULL,
	principioAttivo CHAR(15),
	dataScadenza DATE NOT NULL,
	disponibilita INT NOT NULL
);
INSERT INTO Farmaco(nome, principioAttivo, dataScadenza, disponibilita) VALUES('Aspirina 400 MG Compresse', '', '2022-11-23', 20);
INSERT INTO Farmaco(nome, principioAttivo, dataScadenza, disponibilita) VALUES('Vicks Sinex Flacone', '', '2022-11-24', 30);
INSERT INTO Farmaco(nome, principioAttivo, dataScadenza, disponibilita) VALUES('Vicks Sinex Flacone', '', '2022-07-15', 10);
INSERT INTO Farmaco(nome, principioAttivo, dataScadenza, disponibilita) VALUES('Vicks Sinex Flacone', '', '2022-11-25', 25);
/*INSERT INTO Farmaco(nome, principioAttivo, dataScadenza, disponibilita) VALUES('Aspirina 400 MG Compresse', '', '2022-07-23', 250);
INSERT INTO Farmaco(nome, principioAttivo, dataScadenza, disponibilita) VALUES('Vicks Sinex Flacone', '', '2022-09-15', 250);
INSERT INTO Farmaco(nome, principioAttivo, dataScadenza, disponibilita) VALUES('Rinazina Spray Nasale', '', '2022-09-27', 250);*/


CREATE TABLE Ordine
(
	idOrdine INT NOT NULL PRIMARY KEY,
	stato CHAR(20) NOT NULL,
	dataConsegna DATE NOT NULL,
	refCorriere INT NOT NULL,
	refFarmacia INT NOT NULL,
	FOREIGN KEY(refCorriere) REFERENCES Corriere(idUtente),
	FOREIGN KEY(refFarmacia) REFERENCES Farmacia(idFarmacia)
);

CREATE TABLE Compone
(
	idCompone INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	quantita INT NOT NULL,
	refFarmaco INT NOT NULL,
	refOrdine INT NOT NULL,
	FOREIGN KEY(refFarmaco) REFERENCES Farmaco(idFarmaco),
	FOREIGN KEY(refOrdine) REFERENCES Ordine(idOrdine)
);
