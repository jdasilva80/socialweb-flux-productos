package com.jdasilva.socialweb.webflux.productos.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
//import java.nio.file.Path;
import java.util.Date;
import java.util.List;

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

import com.jdasilva.socialweb.commons.models.productos.entity.Categoria;
import com.jdasilva.socialweb.commons.models.productos.entity.Producto;
import com.jdasilva.socialweb.webflux.productos.services.IProductoService;
import com.jdasilva.socialweb.webflux.productos.services.IUploadService;

@Controller
@SessionAttributes(names = { "producto" })
@RequestMapping("/productos")
public class ProductosController {

	@Autowired
	private IProductoService productoService;

	@Autowired
	IUploadService uploadService;
	
	private static final Logger log = LoggerFactory.getLogger(ProductosController.class);

	@ModelAttribute("categorias")
	public List<Categoria> categorias() {

		return productoService.findAllCategoria();
	}

	@GetMapping("/form")
	public String crear(Model model) {

		model.addAttribute("titulo", "alta producto");
		model.addAttribute("producto", new Producto());
		model.addAttribute("boton", "crear");

		return "form";
	}

	@PostMapping("/form")
	public String guardar(@Valid Producto producto, BindingResult result, Model model, @RequestPart FilePart file,
			SessionStatus status) {

		if (result.hasErrors()) {

			model.addAttribute("titulo", "Error al guardar el producto.");
			model.addAttribute("boton", "guardar");
			return "/form";

		} else {

			status.setComplete();

			Categoria categoria  = productoService.findCategoriaById(producto.getCategoria().getId());
			
			if (producto.getCreateAt() == null) {

				producto.setCreateAt(new Date());
			}
			producto.setCategoria(categoria);

			if (!file.filename().isEmpty()) {

	//			producto.setFoto(UUID.randomUUID().toString().concat("-").concat(file.filename().replace(" ", "")));
	//				}
				try {
					producto.setFoto(uploadService.copy(file));
				} catch (IOException e) {
						log.info("No se ha podido copiar el archivo ,".concat(file.filename()));
				}
			}
			productoService.save(producto);				
			
			log.info("se ha guardado el producto " + producto.getId());
			
			return "redirect:/api/socialweb-productos/productos/listar?success=guardado+correctamente";
		}
	}

	@GetMapping("/form/{id}")
	public String editar(@PathVariable Long id, Model model) {

		Producto producto = productoService.findById(id);
		log.info("producto ".concat(producto.getNombre()));
		model.addAttribute("producto", producto);
		model.addAttribute("titulo", "Producto");
		model.addAttribute("boton", "editar");

		return "form";
	}

	@GetMapping("/verfoto/{foto}")
	public ResponseEntity<Resource> verFoto(@PathVariable String foto) throws MalformedURLException {

//		Path ruta = Paths.get("c://temp/").resolve(foto).toAbsolutePath();
//
//		Resource imagen = new UrlResource(ruta.toUri());
		Resource imagen = uploadService.load(foto);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename:\"" + imagen.getFilename())
				.body(imagen);

	}

	@GetMapping("/ver/{id}")
	public String ver(@PathVariable Long id, Model model) {

		Producto producto = productoService.findById(id);
		
		if(producto != null) {
		
			model.addAttribute("titulo", "detalle producto");
			model.addAttribute("producto", producto);
			return "ver";

		}
		return "redirect:/api/socialweb-productos/productos/listar?error=no+existe+el+id+de+producto";
	}

	@GetMapping("/eliminar/{id}")
	public String delete(@PathVariable Long id) {
		
		Producto producto = productoService.findById(id);

		if(producto != null) {
			productoService.delete(producto);
			return "redirect:/api/socialweb-productos/productos/listar?success=producto+eliminado";
			
		}			
		return "redirect:/api/socialweb-productos/productos/listar?error=no+existe+el+id+de+producto";
		
	}

	@RequestMapping({ "/listar-data-driven" })
	public String listarDataDriven(Model model) {

		model.addAttribute("titulo", "productos");
		List<Producto> productos = productoService.findAllCaseRepeat();
		model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos, 2));

		return "listar";
	}

	@RequestMapping({ "/listar", "/" })
	public String listar(Model model) {

		model.addAttribute("titulo", "productos");
		List<Producto> productos = productoService.findAll();
		model.addAttribute("productos", productos);

		return "listar";
	}

	@RequestMapping({ "/listar-full" })
	public String listarFull(Model model) {

		model.addAttribute("titulo", "productos");
		List<Producto> productos = productoService.findAllCaseRepeat();
		model.addAttribute("productos", productos);

		return "listar";
	}

	@RequestMapping({ "/listar-chunked" })
	public String listarChunked(Model model) {

		model.addAttribute("titulo", "productos");
		List<Producto> productos = productoService.findAllCaseRepeat();
		model.addAttribute("productos", productos);

		return "listar-chunked";
	}
}