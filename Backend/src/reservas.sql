CREATE DATABASE sistemaReservas

USE sistemaReservas

CREATE TABLE Usuario (
    id VARCHAR(20) PRIMARY KEY,
    clave VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    CHECK (rol IN ('ADMINISTRADOR', 'FUNCIONARIO'))
);

CREATE TABLE Categoria (
    id VARCHAR(20) PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE Recurso (
    id VARCHAR(20) PRIMARY KEY,
    categoria VARCHAR(20) NOT NULL,
    descripcion VARCHAR(100) NOT NULL,
    FOREIGN KEY (categoria) REFERENCES Categoria(id)
);

CREATE TABLE Reserva (
    id VARCHAR(20) PRIMARY KEY,
    usuario VARCHAR(20) NOT NULL,
    actividad VARCHAR(100) NOT NULL,
    fecha DATE NOT NULL,
    horaInicio TIME NOT NULL,
    horaFin TIME NOT NULL,
    FOREIGN KEY (usuario) REFERENCES Usuario(id),
    CHECK (horaInicio < horaFin)
);

CREATE TABLE ReservaRecurso (
    reserva VARCHAR(20) NOT NULL,
    recurso VARCHAR(20) NOT NULL,
    PRIMARY KEY (reserva, recurso),
    FOREIGN KEY (reserva) REFERENCES Reserva(id) ON DELETE CASCADE,
    FOREIGN KEY (recurso) REFERENCES Recurso(id)
);
