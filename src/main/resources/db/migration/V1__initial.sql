CREATE TABLE caracterizacion (
 id BIGSERIAL PRIMARY KEY,
 tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('EMPRESA','TALENTO')),
 nombre VARCHAR(250) NOT NULL,
 documento VARCHAR(100),
 payload JSONB NOT NULL,
 creado_en TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_caracterizacion_tipo ON caracterizacion(tipo);
CREATE INDEX idx_caracterizacion_creado ON caracterizacion(creado_en DESC);
CREATE INDEX idx_caracterizacion_payload ON caracterizacion USING GIN(payload);
