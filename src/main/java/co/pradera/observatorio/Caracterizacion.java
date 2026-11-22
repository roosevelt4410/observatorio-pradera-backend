package co.pradera.observatorio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Map;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "caracterizacion")
public class Caracterizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 20)
    public String tipo;

    @Column(nullable = false, length = 250)
    public String nombre;

    @Column(length = 100)
    public String documento;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    public Map<String, Object> payload;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    public OffsetDateTime creadoEn;
}
