package com.jdasilva.socialweb.webflux.productos.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.jdasilva.socialweb.commons.models.productos.entity.Producto;
import com.jdasilva.socialweb.webflux.productos.services.IProductoService;
import com.jdasilva.socialweb.webflux.productos.services.IUploadService;

@RestController
@RequestMapping("/apirest/productos")
public class ProductosRestController {

	private static final Logger log = LoggerFactory.getLogger(ProductosRestController.class);
	
	@Autowired
	IProductoService productoService;

	@Autowired
	IUploadService uploadService;
	
	@GetMapping(value = { "/prueba" })
	public Integer cargarProductosXml() {

		return 1;
	}

	@GetMapping()
	public ResponseEntity<List<Producto>> listar() {

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(productoService.findAll());

	}

	@GetMapping("/listar")
	public List<Producto> listarTodo() {

		return productoService.findAll();

	}

	@GetMapping("/listar-no-flux")
	public List<Producto> listarNoFLux() {

		return productoService.findAll();

	}

	@GetMapping("/findById-no-flux/{productoId}")
	public Producto findByIdNoFlux(@PathVariable Long productoId) {

		return productoService.findById(productoId);

	}

	@GetMapping("/nombre/{term}")
	public List<Producto> findProductosByNombreNoFLux(@PathVariable String term) {

		return productoService.findByNombre(term);

	}

	@GetMapping("/ver/{id}")
	public ResponseEntity<Producto> ver(@PathVariable Long id) {

		Producto producto = productoService.findById(id);
		
		if (producto !=null ) {
			return  ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(producto);
		}
		return ResponseEntity.notFound().build();

	}

	@PostMapping("/validacion")
	public ResponseEntity<Map<String, Object>> post(@RequestBody @Valid Producto producto, BindingResult result) {

		Map<String, Object> response = new HashMap<String, Object>();

		if (result.hasErrors()) {

			response.put("titulo", "Crear producto: rectificar errores");
			Map<String, String> errors = new HashMap<>();
			result.getFieldErrors().forEach((error) -> errors.put(error.getField(),
					"campo ".concat(error.getField()).concat(" : ").concat(error.getDefaultMessage())));

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return ResponseEntity.created(URI.create("/api/productos/".concat(producto.getId().toString())))
				.contentType(MediaType.APPLICATION_JSON).body(response);		

	}

	@PostMapping("")
	public ResponseEntity<Producto> post(@RequestBody Producto producto) {

		Producto p = productoService.save(producto);
		
		if (p !=null ) {
			
			return ResponseEntity.created(URI.create("/apirest/productos".concat(p.getId().toString()))).contentType(MediaType.APPLICATION_JSON).body(p);
		}
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<Producto> put(@RequestBody Producto producto, @PathVariable Long id) {
		
		Producto p = productoService.findById(id);
		
		if (p !=null ) {

			p.setNombre(producto.getNombre());
			p.setCategoria(producto.getCategoria());
			p.setPrecio(producto.getPrecio());
			productoService.save(p);
			
			return ResponseEntity.created(URI.create("/apirest/productos".concat(p.getId().toString())))
					.contentType(MediaType.APPLICATION_JSON).body(p);

		}
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {

		Producto p = productoService.findById(id);
		
		if (p != null ) {

			productoService.delete(p);
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);

		}
		return ResponseEntity.notFound().build();
	}

	@PostMapping("/v2")
	public ResponseEntity<Producto> post(Producto producto, @RequestPart FilePart file) {

		if (producto != null ) {			

			producto.setFoto(file.filename());
			file.transferTo(new File("c://temp//" + UUID.randomUUID() + "_" + file.filename()));
			productoService.save(producto);
			return ResponseEntity.created(URI.create("/apirest/productos".concat(producto.getId().toString())))
					.contentType(MediaType.APPLICATION_JSON).body(producto);
			
		}
		return ResponseEntity.notFound().build();

	}

	@PostMapping("/uploads/{id}")
	public ResponseEntity<Producto> upload(@PathVariable Long id, @RequestPart FilePart file) {

		Producto producto = productoService.findById(id);
		
		if (producto != null ) {
			
			try {
				producto.setFoto(uploadService.copy(file));
			} catch (IOException e) {
				log.info("No se ha podido copiar el archivo ,".concat(file.name()));
			}
			productoService.save(producto);
			return ResponseEntity.ok(producto);
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/uploads/{filename:.+}")
	public ResponseEntity<Resource> verArchivo(@PathVariable String filename) {

		Resource recurso = null;

		recurso = uploadService.load(filename);

		if (recurso != null) {
			
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename:\"" .concat(recurso.getFilename()))
					.body(recurso);

		} else {

			return ResponseEntity.noContent().build();
		}
	}
}
