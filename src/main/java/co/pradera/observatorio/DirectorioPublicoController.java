package co.pradera.observatorio;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Solo publica datos de clasificacion empresarial, no datos personales. */
@RestController
@RequestMapping("/api/v1/directorio")
public class DirectorioPublicoController {
    private final CaracterizacionRepository repository;

    public DirectorioPublicoController(CaracterizacionRepository repository) {
        this.repository = repository;
    }

    public record EmpresaPublica(String nombre, String actividad, String codigoCiiu,
                                 String sector, String tamano, String fechaMatricula,
                                 String fechaRenovacion) {}

    @GetMapping("/empresas")
    public Page<EmpresaPublica> empresas(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "100") int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paginacion invalida");
        }
        return repository.findByTipo("EMPRESA", PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "id")))
            .map(c -> {
                Map<String, Object> datos = c.payload == null ? Map.of() : c.payload;
                return new EmpresaPublica(
                    texto(datos, "nombreEmpresa", c.nombre),
                    texto(datos, "actividadEconomica", "Sin actividad registrada"),
                    texto(datos, "codigoCiiu", ""),
                    texto(datos, "sectorEconomico", "Sin sector registrado"),
                    texto(datos, "tamanoEmpresa", "Sin tamano registrado"),
                    null, null
                );
            });
    }

    private static String texto(Map<String, Object> datos, String clave, String defecto) {
        Object valor = datos.get(clave);
        return valor == null || valor.toString().isBlank() ? defecto : valor.toString().trim();
    }
}
