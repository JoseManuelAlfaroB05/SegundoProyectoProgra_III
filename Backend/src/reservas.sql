CREATE DATABASE IF NOT EXISTS sistemaReservas;

USE sistemareservas;

CREATE TABLE IF NOT EXISTS Usuario (
    id VARCHAR(20) PRIMARY KEY,
    clave VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    CHECK (rol IN ('ADMINISTRADOR', 'FUNCIONARIO'))
);

CREATE TABLE IF NOT EXISTS Categoria (
    id VARCHAR(20) PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS Recurso (
    id VARCHAR(20) PRIMARY KEY,
    categoria VARCHAR(20) NOT NULL,
    descripcion VARCHAR(100) NOT NULL,
    FOREIGN KEY (categoria) REFERENCES Categoria(id)
);

CREATE TABLE IF NOT EXISTS Reserva (
    id VARCHAR(20) PRIMARY KEY,
    usuario VARCHAR(20) NOT NULL,
    actividad VARCHAR(100) NOT NULL,
    fecha DATE NOT NULL,
    horaInicio TIME NOT NULL,
    horaFin TIME NOT NULL,
    FOREIGN KEY (usuario) REFERENCES Usuario(id),
    CHECK (horaInicio < horaFin)
);

CREATE TABLE IF NOT EXISTS ReservaRecurso (
    reserva VARCHAR(20) NOT NULL,
    recurso VARCHAR(20) NOT NULL,
    PRIMARY KEY (reserva, recurso),
    FOREIGN KEY (reserva) REFERENCES Reserva(id) ON DELETE CASCADE,
    FOREIGN KEY (recurso) REFERENCES Recurso(id)
);
