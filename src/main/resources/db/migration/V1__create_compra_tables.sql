CREATE TABLE compra (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    proveedor_rut VARCHAR(12) NOT NULL,
    proveedor_nombre VARCHAR(120) NOT NULL,
    proveedor_contacto VARCHAR(120),
    fecha_compra DATETIME NOT NULL,
    costo_transporte DECIMAL(12,2) NOT NULL,
    total_productos DECIMAL(12,2) NOT NULL,
    total_compra DECIMAL(12,2) NOT NULL,
    estado_compra VARCHAR(20) NOT NULL,
    usuario_rut VARCHAR(12) NOT NULL,
    notas VARCHAR(500)
);

CREATE INDEX idx_compra_proveedor_rut ON compra (proveedor_rut);
CREATE INDEX idx_compra_estado ON compra (estado_compra);

CREATE TABLE detalle_compra (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    compra_id BIGINT NOT NULL,
    codigo_pastel VARCHAR(30) NOT NULL,
    nombre_producto VARCHAR(120) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(12,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_detalle_compra_compra FOREIGN KEY (compra_id) REFERENCES compra(id) ON DELETE CASCADE
);

CREATE INDEX idx_detalle_compra_compra_id ON detalle_compra (compra_id);
