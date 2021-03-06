package com.jdasilva.socialweb.webflux.productos.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.time.Duration;
import java.util.Date;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
//import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import com.jdasilva.socialweb.commons.models.document.Categoria;
import com.jdasilva.socialweb.commons.models.document.Producto;
import com.jdasilva.socialweb.webflux.productos.services.IProductoReactiveService;
import com.jdasilva.socialweb.webflux.productos.services.IUploadService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@SessionAttributes(names = { "producto" })
@RequestMapping("/reactive/productos")
public class ProductosReactiveController {

	@Autowired
	private IProductoReactiveService productoService;

	@Autowired
	IUploadService uploadService;
	
	private static final Logger log = LoggerFactory.getLogger(ProductosReactiveController.class);

	@ModelAttribute("categorias")
	public Flux<Categoria> categorias() {

		return productoService.findAllCategoriaReactive();
	}

	@GetMapping("/form")
	public Mono<String> crear(Model model) {

		model.addAttribute("titulo", "alta producto");
		model.addAttribute("producto", new Producto());
		model.addAttribute("boton", "crear");

		return Mono.just("form");
	}

	@PostMapping("/form")
	public Mono<String> guardar(@Valid Producto producto, BindingResult result, Model model, @RequestPart FilePart file,
			SessionStatus status) {

		if (result.hasErrors()) {

			model.addAttribute("titulo", "Error al guardar el producto.");
			model.addAttribute("boton", "guardar");
			return Mono.just("/form");

		} else {

			status.setComplete();

			return productoService.findCategoriaByIdReactive(producto.getCategoria().getId()).flatMap(c -> {
				if (producto.getCreateAt() == null) {

					producto.setCreateAt(new Date());
				}
				producto.setCategoria(c);

				if (!file.filename().isEmpty()) {

	//					producto.setFoto(UUID.randomUUID().toString().concat("-").concat(file.filename().replace(" ", "")));
	//				}
					try {
						producto.setFoto(uploadService.copy(file));
					} catch (IOException e) {
						log.info("No se ha podido copiar el archivo ,".concat(file.filename()));
					}
				}
				return productoService.saveReactive(producto);
				
			})
			.doOnNext(p -> log.info("se ha guardado el producto " + p.getId()))
			.thenReturn("redirect:/api/socialweb-productos/reactive/productos/listar?success=guardado+correctamente");
		}
	}

	@GetMapping("/form/{id}")
	public Mono<String> editar(@PathVariable String id, Model model) {

		Mono<Producto> producto = productoService.findByIdReactive(id)
				.doOnNext(p -> log.info("producto ".concat(p.getNombre()))).defaultIfEmpty(new Producto());
		model.addAttribute("producto", producto);
		model.addAttribute("titulo", "Producto");
		model.addAttribute("boton", "editar");

		return Mono.just("form");
	}

	@GetMapping("/reactiveform/{id}")
	public Mono<String> editarReact(@PathVariable String id, Model model) {

		return productoService.findByIdReactive(id).doOnNext(p -> {

			log.info("producto ".concat(p.getNombre()));
			model.addAttribute("producto", p);
			model.addAttribute("titulo", "Producto");
			model.addAttribute("boton", "editar");

		}).defaultIfEmpty(new Producto()).flatMap((p) -> {
			if (p.getId() == null) {
				return Mono.error(new InterruptedException("no existe el id de producto"));
			}
			return Mono.just(p);
		}).then(Mono.just("form"))
				.onErrorResume((e) -> Mono.just("redirect:/api/socialweb-productos/reactive/productos/listar?error=no+existe+el+id+de+producto"));

	}

	@GetMapping("/verfoto/{foto}")
	public Mono<ResponseEntity<Resource>> verFoto(@PathVariable String foto) throws MalformedURLException {

//		Path ruta = Paths.get("c://temp/").resolve(foto).toAbsolutePath();
//
//		Resource imagen = new UrlResource(ruta.toUri());
		Resource imagen = uploadService.load(foto);

		return Mono.just(ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename:\"" + imagen.getFilename())
				.body(imagen));

	}

	@GetMapping("/ver/{id}")
	public Mono<String> ver(@PathVariable String id, Model model) {

		return productoService.findByIdReactive(id).doOnNext(

				p -> {
					model.addAttribute("titulo", "detalle producto");
					model.addAttribute("producto", p);

				}).defaultIfEmpty(new Producto()).flatMap((p) -> {
					if (p.getId() == null) {
						return Mono.error(new InterruptedException("no existe el id de producto"));
					}
					return Mono.just(p);
				}).then(Mono.just("ver"))
				.onErrorResume((e) -> Mono.just("redirect:/api/socialweb-productos/reactive/productos/listar?error=no+existe+el+id+de+producto"));
	}

	@GetMapping("/eliminar/{id}")
	public Mono<String> delete(@PathVariable String id) {

		return productoService.findByIdReactive(id).defaultIfEmpty(new Producto()).flatMap((p) -> {
			if (p.getId() == null) {
				return Mono.error(new InterruptedException("no existe el id de producto"));
			}
			return Mono.just(p);
		}).flatMap(p -> productoService.deleteReactive(p)).thenReturn("redirect:/api/socialweb-productos/reactive/productos/listar?success=producto+eliminado")
				.onErrorResume((e) -> Mono.just("redirect:/api/socialweb-productos/reactive/productos/listar?error=no+existe+el+id+de+producto"));

	}

	@RequestMapping({ "/listar-data-driven" })
	public String listarDataDriven(Model model) {

		model.addAttribute("titulo", "productos");
		Flux<Producto> productos = productoService.findAllUpperCaseReactive().delayElements(Duration.ofSeconds(2));
		productos.subscribe(producto -> log.info(producto.getNombre()));
		model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos, 2));

		return "listar";
	}

	@RequestMapping({ "/listar", "/" })
	public String listar(Model model) {

		model.addAttribute("titulo", "productos");
		Flux<Producto> productos = productoService.findAllReactive();
		productos.subscribe(producto -> log.info(producto.getNombre()));
		model.addAttribute("productos", productos);

		return "listar";
	}

	@RequestMapping({ "/listar-full" })
	public String listarFull(Model model) {

		model.addAttribute("titulo", "productos");
		Flux<Producto> productos = productoService.findAllUpperCaseRepeatReactive();
		productos.subscribe(producto -> log.info(producto.getNombre()));
		model.addAttribute("productos", productos);

		return "listar";
	}

	@RequestMapping({ "/listar-chunked" })
	public String listarChunked(Model model) {

		model.addAttribute("titulo", "productos");
		Flux<Producto> productos = productoService.findAllUpperCaseRepeatReactive();
		productos.subscribe(producto -> log.info(producto.getNombre()));
		model.addAttribute("productos", productos);

		return "listar-chunked";
	}
}