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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.jdasilva.socialweb.commons.models.document.Producto;
import com.jdasilva.socialweb.webflux.productos.services.IProductoReactiveService;
import com.jdasilva.socialweb.webflux.productos.services.IProductoService;
import com.jdasilva.socialweb.webflux.productos.services.IUploadService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/reactive/apirest/productos")
public class ProductosRestReactiveController {

	private static final Logger log = LoggerFactory.getLogger(ProductosRestReactiveController.class);

	@Autowired
	IProductoReactiveService productoReactiveService;
	
	@Autowired
	IProductoService productoService;

	@Autowired
	IUploadService uploadService;
	
	@GetMapping(value = { "/prueba" })
	public Integer cargarProductosXml() {

		return 1;
	}

	@GetMapping()
	public Mono<ResponseEntity<Flux<Producto>>> listar() {

		return Mono.just(
				ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(productoReactiveService.findAllReactive()));

	}

	@GetMapping("/listar")
	public Flux<Producto> listarTodo() {

		return productoReactiveService.findAllReactive();

	}

	@GetMapping("/listar-no-flux")
	public List<com.jdasilva.socialweb.commons.models.productos.entity.Producto> listarNoFLux() {

		return productoService.findAll();

	}

	@GetMapping("/findById-no-flux/{productoId}")
	public com.jdasilva.socialweb.commons.models.productos.entity.Producto findByIdNoFlux(@PathVariable Long productoId) {

		return productoService.findById(productoId);

	}

	@GetMapping("/nombre/{term}")
	public List<com.jdasilva.socialweb.commons.models.productos.entity.Producto> findProductosByNombreNoFLux(@PathVariable String term) {

		return productoService.findByNombre(term);

	}

	@GetMapping("/ver/{id}")
	public Mono<ResponseEntity<Producto>> ver(@PathVariable String id) {

		return productoReactiveService.findByIdReactive(id)
				.map(p -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());

	}

	@PostMapping("/validacion")
	public Mono<ResponseEntity<Map<String, Object>>> post(@RequestBody @Valid Mono<Producto> monoProducto) {

		Map<String, Object> response = new HashMap<String, Object>();

		return monoProducto.flatMap(

				producto -> {

					return productoReactiveService.saveReactive(producto).map(p -> {
						response.put("producto", p);
						return ResponseEntity.created(URI.create("/reactive/apirest/productos".concat(p.getId())))
								.contentType(MediaType.APPLICATION_JSON).body(response);
					});

				}).onErrorResume(t -> {
					return Mono.just(t).cast(WebExchangeBindException.class).flatMap(e -> {
						return Mono.just(e.getFieldErrors());
					}).flatMapMany(Flux::fromIterable).map(e -> {
						return "el campo ".concat(e.getField()).concat(" ").concat(e.getDefaultMessage());
					}).collectSortedList().flatMap(errorsList -> {
						response.put("errores", errorsList);
						return Mono.just(ResponseEntity.badRequest().body(response));
					});
				});

	}

	@PostMapping("")
	public Mono<ResponseEntity<Producto>> post(@RequestBody Producto producto) {

		return productoReactiveService.saveReactive(producto).map(p -> {
			return ResponseEntity.created(URI.create("/reactive/apirest/productos".concat(p.getId())))
					.contentType(MediaType.APPLICATION_JSON).body(p);
		});

	}

	@PutMapping("/{id}")
	public Mono<ResponseEntity<Producto>> put(@RequestBody Producto producto, @PathVariable String id) {

		return productoReactiveService.findByIdReactive(id).flatMap(p -> {

			p.setNombre(producto.getNombre());
			p.setCategoria(producto.getCategoria());
			p.setPrecio(producto.getPrecio());
			return productoReactiveService.saveReactive(p);

		}).map(p -> {
			return ResponseEntity.created(URI.create("/reactive/apirest/productos".concat(p.getId())))
					.contentType(MediaType.APPLICATION_JSON).body(p);
		}).defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {

		return productoReactiveService.findByIdReactive(id).flatMap(p -> {

			return productoReactiveService.deleteReactive(p).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));

		}).defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PostMapping("/v2")
	public Mono<ResponseEntity<Producto>> post(Producto producto, @RequestPart FilePart file) {

		producto.setFoto(file.filename());

		return file.transferTo(new File("c://temp//" + UUID.randomUUID() + "_" + file.filename()))
				.then(productoReactiveService.saveReactive(producto))
				.map(p -> ResponseEntity.created(URI.create("/reactive/apirest/productos".concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON).body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());

	}

	@PostMapping("/uploads/{id}")
	public Mono<ResponseEntity<Producto>> upload(@PathVariable String id, @RequestPart FilePart file) {

		return productoReactiveService.findByIdReactive(id).flatMap(

				p -> {
//					p.setFoto(file.filename());
//					return file.transferTo(new File("c://temp//" + UUID.randomUUID() + "_" + file.filename()))
//							.then(productoService.saveReactive(p));
					try {
						p.setFoto(uploadService.copy(file));
					} catch (IOException e) {
						log.info("No se ha podido copiar el archivo ,".concat(file.name()));
					}
					return productoReactiveService.saveReactive(p);
				}

		).map(p -> ResponseEntity.ok(p)).defaultIfEmpty(ResponseEntity.notFound().build());
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
