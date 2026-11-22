package co.pradera.observatorio;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Solo publica información agregada; nunca documentos ni formularios completos. */
@RestController
@RequestMapping("/api/v1/indicadores")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
public class AnaliticaController {
    private final CaracterizacionRepository repository;

    public AnaliticaController(CaracterizacionRepository repository) {
        this.repository = repository;
    }

    public record Distribucion(String nombre, long cantidad) {}
    public record Resumen(long empresas, long talento, List<Distribucion> sectores, List<Distribucion> tamanos) {}

    @GetMapping("/analitica")
    public Resumen analitica() {
        // Los agregados se calculan en PostgreSQL, sin transferir formularios personales al navegador.
        return new Resumen(
            repository.countByTipo("EMPRESA"),
            repository.countByTipo("TALENTO"),
            convertir(repository.agruparEmpresasPorSector()),
            convertir(repository.agruparEmpresasPorTamano())
        );
    }

    private List<Distribucion> convertir(List<Object[]> filas) {
        List<Distribucion> resultado = new ArrayList<>();
        for (Object[] fila : filas) {
            String nombre = fila[0] == null ? "Sin especificar" : fila[0].toString();
            long cantidad = ((Number) fila[1]).longValue();
            resultado.add(new Distribucion(nombre, cantidad));
        }
        return resultado;
    }
}
