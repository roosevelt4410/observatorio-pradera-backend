package co.pradera.observatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface CaracterizacionRepository extends JpaRepository<Caracterizacion,Long> {
 Page<Caracterizacion> findByTipo(String tipo, Pageable pageable);
 long countByTipo(String tipo);
 @Query(value = """
 SELECT * FROM caracterizacion c
 WHERE c.tipo = :tipo
 AND (:busqueda = '' OR LOWER(COALESCE(c.nombre,'')) LIKE CONCAT('%', LOWER(:busqueda), '%')
 OR LOWER(COALESCE(c.documento,'')) LIKE CONCAT('%', LOWER(:busqueda), '%')
 OR LOWER(COALESCE(c.payload->>'sectorEconomico','')) LIKE CONCAT('%', LOWER(:busqueda), '%')
 OR LOWER(COALESCE(c.payload->>'municipio','')) LIKE CONCAT('%', LOWER(:busqueda), '%'))
 AND (:sector = '' OR LOWER(COALESCE(c.payload->>'sectorEconomico','')) = LOWER(:sector))
 AND (:tamano = '' OR LOWER(COALESCE(c.payload->>'tamanoEmpresa','')) = LOWER(:tamano))
 """, countQuery = """
 SELECT COUNT(*) FROM caracterizacion c
 WHERE c.tipo = :tipo
 AND (:busqueda = '' OR LOWER(COALESCE(c.nombre,'')) LIKE CONCAT('%', LOWER(:busqueda), '%')
 OR LOWER(COALESCE(c.documento,'')) LIKE CONCAT('%', LOWER(:busqueda), '%')
 OR LOWER(COALESCE(c.payload->>'sectorEconomico','')) LIKE CONCAT('%', LOWER(:busqueda), '%')
 OR LOWER(COALESCE(c.payload->>'municipio','')) LIKE CONCAT('%', LOWER(:busqueda), '%'))
 AND (:sector = '' OR LOWER(COALESCE(c.payload->>'sectorEconomico','')) = LOWER(:sector))
 AND (:tamano = '' OR LOWER(COALESCE(c.payload->>'tamanoEmpresa','')) = LOWER(:tamano))
 """, nativeQuery = true)
 Page<Caracterizacion> buscar(@org.springframework.data.repository.query.Param("tipo") String tipo,
 @org.springframework.data.repository.query.Param("busqueda") String busqueda,
 @org.springframework.data.repository.query.Param("sector") String sector,
 @org.springframework.data.repository.query.Param("tamano") String tamano, Pageable pageable);
 @Query(value = "SELECT COALESCE(NULLIF(TRIM(payload->>'sectorEconomico'), ''), 'Sin especificar') AS nombre, COUNT(*) AS cantidad FROM caracterizacion WHERE tipo = 'EMPRESA' GROUP BY 1 ORDER BY 2 DESC, 1 ASC LIMIT 20", nativeQuery = true)
 List<Object[]> agruparEmpresasPorSector();
 @Query(value = "SELECT COALESCE(NULLIF(TRIM(payload->>'tamanoEmpresa'), ''), 'Sin especificar') AS nombre, COUNT(*) AS cantidad FROM caracterizacion WHERE tipo = 'EMPRESA' GROUP BY 1 ORDER BY 2 DESC, 1 ASC LIMIT 20", nativeQuery = true)
 List<Object[]> agruparEmpresasPorTamano();
}
