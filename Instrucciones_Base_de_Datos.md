# Guía de instalación y configuración
## Sistema de Reserva de Recursos

Esta guía explica cómo instalar y preparar la base de datos del Sistema de Reserva de Recursos para poder ejecutar el proyecto desde cero.

## 1. Requisitos previos

Antes de comenzar, se necesita:

- MySQL Server instalado.
- MySQL Workbench o MySQL Command Line Client.
- Java JDK compatible con el proyecto.
- IntelliJ IDEA (recomendado).

## 2. Crear la base de datos

Abrir MySQL Workbench o la terminal de MySQL y ejecutar:

```sql
CREATE DATABASE sistemaReservas
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE sistemaReservas;
```

Esto crea la base de datos que utilizará el sistema.

## 3. Crear las tablas

Después de crear y seleccionar la base de datos, ejecutar el script de creación de tablas proporcionado con el proyecto.

El script debe crear las siguientes tablas:

- `Usuario`
- `Categoria`
- `Recurso`
- `Reserva`
- `ReservaRecurso`

Es importante respetar las claves primarias, claves foráneas y restricciones definidas en el diseño de la base de datos.

## 4. Insertar el administrador inicial

Para poder administrar el sistema, es necesario registrar al menos un usuario con el rol `ADMINISTRADOR`.

Ejecutar:

```sql
INSERT INTO Usuario (id, clave, rol, nombre, telefono)
VALUES (
    'ADMIN001',
    'Admin123',
    'ADMINISTRADOR',
    'Administrador del Sistema',
    '88888888'
);
```

Credenciales iniciales:

| Campo | Valor |
|---|---|
| Usuario | `ADMIN001` |
| Contraseña | `Admin123` |
| Rol | ADMINISTRADOR |

## 5. Insertar un funcionario inicial

Los funcionarios utilizan su identificación como contraseña inicial.

Ejecutar:

```sql
INSERT INTO Usuario (id, clave, rol, nombre, telefono)
VALUES (
    'F001',
    'F001',
    'FUNCIONARIO',
    'Funcionario de Prueba',
    '87777777'
);
```

Credenciales iniciales:

| Campo | Valor |
|---|---|
| Usuario | `F001` |
| Contraseña | `F001` |
| Rol | FUNCIONARIO |

## 6. Verificar los usuarios registrados

Ejecutar:

```sql
SELECT id, clave, rol, nombre, telefono
FROM Usuario;
```

Deben aparecer los dos usuarios iniciales.

## 7. Configurar la conexión a MySQL

En el módulo Backend, localizar el archivo `database.properties`.

Configurar los datos de conexión según la instalación local:

```properties
database_driver=com.mysql.cj.jdbc.Driver
database_server=localhost
database_port=3306
database_user=root
database_password=TU_CONTRASEÑA_MYSQL
database_name=sistemaReservas
```

Reemplazar `TU_CONTRASEÑA_MYSQL` por la contraseña configurada en MySQL.

## 8. Ejecutar el sistema

Una vez preparada la base de datos:

1. Abrir el proyecto en IntelliJ IDEA.
2. Ejecutar el módulo Backend.
3. Verificar que el servidor haya iniciado correctamente.
4. Ejecutar el módulo Frontend.
5. Iniciar sesión con alguna de las credenciales iniciales.

## 9. Credenciales iniciales

| Tipo de usuario | Usuario | Contraseña |
|---|---|---|
| Administrador | `ADMIN001` | `Admin123` |
| Funcionario | `F001` | `F001` |

## 10. Consideraciones importantes

- El script de creación de tablas debe ejecutarse antes de insertar usuarios.
- Las identificaciones de los usuarios deben ser únicas.
- El Backend es el único módulo que debe conectarse directamente a MySQL.
- Si el sistema se ejecuta desde otra computadora, se deben ajustar los datos de `database.properties`.
- Las credenciales indicadas son ejemplos para realizar las primeras pruebas.

**Al finalizar estos pasos, el sistema estará preparado para iniciar sesión con un administrador y un funcionario.**