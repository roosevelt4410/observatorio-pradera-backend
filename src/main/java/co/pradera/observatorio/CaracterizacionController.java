package co.pradera.observatorio;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
@RestController @RequestMapping("/api/v1")
@CrossOrigin(origins={"http://localhost:4200","http://127.0.0.1:4200"})
public class CaracterizacionController {
 private final CaracterizacionRepository repository;
 public CaracterizacionController(CaracterizacionRepository repository){this.repository=repository;}
 public record Registro(@NotBlank String nombre, String documento, @NotNull Map<String,Object> datos) {}
 @PostMapping("/caracterizaciones/{tipo}") @ResponseStatus(HttpStatus.CREATED)
 public Caracterizacion crear(@PathVariable String tipo,@Valid @RequestBody Registro req){
  String normalizado=tipo.toUpperCase(); validarTipo(normalizado);
  if(req.datos().isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Los datos no pueden estar vacíos");
  Caracterizacion c=new Caracterizacion(); c.tipo=normalizado;c.nombre=req.nombre().trim();c.documento=req.documento();c.payload=req.datos();
  return repository.saveAndFlush(c);
 }
 @GetMapping("/caracterizaciones/{tipo}")
 public Page<Caracterizacion> listar(@PathVariable String tipo,
 @RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size,
 @RequestParam(defaultValue="") String busqueda,
 @RequestParam(defaultValue="") String sector,
 @RequestParam(defaultValue="") String tamano){
  String normalizado=tipo.toUpperCase();validarTipo(normalizado);
  if(page<0||size<1||size>100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Paginación inválida");
  if(busqueda.length()>150||sector.length()>100||tamano.length()>100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Filtro demasiado largo");
  return repository.buscar(normalizado,busqueda.trim(),sector.trim(),tamano.trim(),
    PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"id")));
 }
 @PutMapping("/caracterizaciones/{tipo}/{id}")
 public Caracterizacion actualizar(@PathVariable String tipo, @PathVariable Long id, @Valid @RequestBody Registro req) {
  String normalizado=tipo.toUpperCase(); validarTipo(normalizado);
  if(req.datos().isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Los datos no pueden estar vacíos");
  Caracterizacion c=repository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro no encontrado"));
  if(!c.tipo.equals(normalizado)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro no encontrado");
  c.nombre=req.nombre().trim(); c.documento=req.documento(); c.payload=req.datos();
  return repository.saveAndFlush(c);
 }
 @GetMapping("/indicadores/resumen") public Map<String,Long> resumen(){
  return Map.of("empresas",repository.countByTipo("EMPRESA"),"talento",repository.countByTipo("TALENTO"));
 }
 @GetMapping("/salud") public Map<String,String> salud(){return Map.of("estado","OK");}
 private void validarTipo(String tipo){if(!tipo.equals("EMPRESA")&&!tipo.equals("TALENTO"))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Tipo no soportado");}
}
