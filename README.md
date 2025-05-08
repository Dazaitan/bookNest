## 🚀 Instalación

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Dazaitan/TaskManager
2. Configurar base de datos:
    * Crear base de datos
      CREATE DATABASE bookNest;
    * Tabla Usuarios
      CREATE TABLE usuarios (
      id SERIAL PRIMARY KEY,
      nombre VARCHAR(100) NOT NULL,
      correo VARCHAR(100) UNIQUE NOT NULL,
      contrasena VARCHAR(255) NOT NULL,
      rol VARCHAR(20) CHECK (rol IN ('ADMIN', 'CLIENTE')) NOT NULL,
      fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    * Tabla Libros
      CREATE TABLE libros (
      id SERIAL PRIMARY KEY,
      titulo VARCHAR(200) NOT NULL,
      autor VARCHAR(100) NOT NULL,
      precio DECIMAL(10,2) NOT NULL,
      stock INT NOT NULL CHECK (stock >= 0),
      fecha_publicacion DATE,
      descripcion TEXT
      );
    * Tabla órdenes
      CREATE TABLE ordenes (
      id SERIAL PRIMARY KEY,
      usuario_id INT REFERENCES usuarios(id),
      estado VARCHAR(20) CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'ENVIADA', 'CANCELADA')) NOT NULL,
      fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    * Tabla ordenes_libros
      CREATE TABLE ordenes_libros (
      id SERIAL PRIMARY KEY,
      orden_id INT REFERENCES ordenes(id) ON DELETE CASCADE,
      libro_id INT REFERENCES libros(id) ON DELETE CASCADE,
      cantidad INT NOT NULL CHECK (cantidad > 0),
      precio_unitario DECIMAL(10,2) NOT NULL
      );
    * Tabla historial_transacciones
      CREATE TABLE historial_transacciones (
      id SERIAL PRIMARY KEY,
      usuario_id INT REFERENCES usuarios(id),
      orden_id INT REFERENCES ordenes(id),
      fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      tipo_transaccion VARCHAR(50) NOT NULL
      );
    * Tabla notificaciones
      CREATE TABLE notificaciones (
      id SERIAL PRIMARY KEY,
      usuario_id INT REFERENCES usuarios(id),
      mensaje TEXT NOT NULL,
      fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      leido BOOLEAN DEFAULT FALSE
      );
3. Pruebas
   Ejecutar todas las pruebas unitarias comando:
    ```bash
    mvn test