CREATE DATABASE DatabaseFarmacia;
USE DatabaseFarmacia;

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

CREATE TABLE Farmacista
(
	idFarmacista INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	email CHAR(30) NOT NULL,
	hashPassword CHAR(40) NOT NULL,
	refFarmacia INT NOT NULL,
	FOREIGN KEY(refFarmacia) REFERENCES Farmacia(idFarmacia)
);

CREATE TABLE Farmaco
(
	idFarmaco INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	nome CHAR(30) NOT NULL,
	principioAttivo CHAR(15),
	dataScadenza DATE NOT NULL,
	disponibilita INT NOT NULL,
	daBanco BOOLEAN NOT NULL,
	refFarmacia INT NOT NULL,
	FOREIGN KEY(refFarmacia) REFERENCES Farmacia(idFarmacia)
);
/*INSERT INTO Farmaco(nome, principioAttivo, dataScadenza, disponibilita, daBanco, refFarmacia) VALUES('Aspirina 400 MG Compresse', '', '2022-06-25', 20, 1, 1);
INSERT INTO Farmaco(nome, principioAttivo, dataScadenza, disponibilita, daBanco, refFarmacia) VALUES('Vicks Sinex Flacone', '', '2022-11-24', 30, 0, 1);
INSERT INTO Farmaco(nome, principioAttivo, dataScadenza, disponibilita, daBanco, refFarmacia) VALUES('Vicks Sinex Flacone', '', '2022-06-23', 10, 0, 1);
INSERT INTO Farmaco(nome, principioAttivo, dataScadenza, disponibilita, daBanco, refFarmacia) VALUES('Vicks Sinex Flacone', '', '2022-11-25', 25, 0, 1);*/
