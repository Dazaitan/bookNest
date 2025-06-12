## 🚀 Instalación

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Dazaitan/TaskManager
2. Configurar base de datos:
    * Crear base de datos
      ```bash
      CREATE DATABASE bookNest;
    * Tabla Usuarios
      ```bash
      CREATE TABLE usuarios (
      id SERIAL PRIMARY KEY,
      nombre VARCHAR(100) NOT NULL,
      correo VARCHAR(100) UNIQUE NOT NULL,
      contrasena VARCHAR(255) NOT NULL,
      rol VARCHAR(20) CHECK (rol IN ('ADMIN', 'CLIENTE')) NOT NULL,
      fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    * Tabla Libros
      ```bash
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
      ```bash
      CREATE TABLE ordenes (
      id SERIAL PRIMARY KEY,
      usuario_id INT REFERENCES usuarios(id),
      estado VARCHAR(20) CHECK (estado IN ('PENDIENTE', 'EN_PROCESO', 'ENVIADA', 'COMPLETADA', 'CANCELADA')) NOT NULL,
      fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    * Tabla ordenes_libros
      ```bash
      CREATE TABLE ordenes_libros (
      id SERIAL PRIMARY KEY,
      orden_id INT REFERENCES ordenes(id) ON DELETE CASCADE,
      libro_id INT REFERENCES libros(id) ON DELETE CASCADE,
      cantidad INT NOT NULL CHECK (cantidad > 0),
      precio_unitario DECIMAL(10,2) NOT NULL
      );
    * Tabla transacciones
      ```bash
      CREATE TABLE transacciones (
      id SERIAL PRIMARY KEY,
      usuario_id BIGINT NOT NULL,
      orden_id BIGINT NOT NULL,
      fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      monto_total DECIMAL(10,2) NOT NULL,
      metodo_pago VARCHAR(50) NOT NULL,
      FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
      FOREIGN KEY (orden_id) REFERENCES ordenes(id) ON DELETE CASCADE
      );

    * Tabla notificaciones
      ```bash
      CREATE TABLE notificaciones (
      id SERIAL PRIMARY KEY,
      usuario_id INT REFERENCES usuarios(id),
      mensaje TEXT NOT NULL,
      fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      leido BOOLEAN DEFAULT FALSE
      );
3. Pruebas
   Ejecutar todas las pruebas unitarias comando(Para ejecutar todos los tests antes debe estar ejecutando Docker desktop):
    ```bash
    mvn test