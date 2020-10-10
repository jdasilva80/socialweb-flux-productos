package com.jdasilva.socialweb.webflux.productos;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.jdasilva.socialweb.commons.models.document.Producto;
import com.jdasilva.socialweb.commons.models.productos.entity.Categoria;
//import com.jdasilva.socialweb.webflux.productos.services.IProductoReactiveService;
import com.jdasilva.socialweb.webflux.productos.services.IProductoService;
import com.jdasilva.socialweb.webflux.productos.services.IUploadService;

//import reactor.core.publisher.Flux;

@EnableEurekaClient
@SpringBootApplication
@EntityScan(basePackageClasses = {Categoria.class, Producto.class})
public class WebfluxApplication implements CommandLineRunner {

//	@Autowired
//	private IProductoReactiveService productoService;
	
	@Autowired
	private IProductoService productoService;

	@Autowired(required = false)
	private ReactiveMongoTemplate reactiveMongoTemplate;
	
	@Autowired
	IUploadService uploadService;

	private final Logger log = LoggerFactory.getLogger(WebfluxApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		uploadService.deleteAll();
		uploadService.init();

//		reactiveMongoTemplate.dropCollection("productos").subscribe();
//		reactiveMongoTemplate.dropCollection("categorias").subscribe();

		Categoria cat1 = new Categoria("deportes");
		Categoria cat2 = new Categoria("electronica");
		Categoria cat3 = new Categoria("viajes");
		Categoria cat4 = new Categoria("muebles");
		
		productoService.save(cat1);
		productoService.save(cat2);
		productoService.save(cat3);
		productoService.save(cat4);

//		Flux.just(cat1, cat2, cat3).flatMap(productoService::saveReactive).doOnNext(c -> log.info(c.getNombre())).subscribe();
//				.thenMany(Flux.just(new Producto("producto 1", 1.23, cat1, "producto1.jpg"), new Producto("producto 2", 1.23, cat2, "producto2.jpg"),
//						new Producto("producto 3", 45.24, cat3, "producto3.jpg"), new Producto("producto 4", 19.43, cat1, "producto4.jpg"),
//						new Producto("producto 5", 16.63, cat1, "producto5.jpg"), new Producto("producto 6", 18.83, cat2, "producto6.jpg"),
//						new Producto("producto 7", 881.89, cat3, "producto7.jpg"), new Producto("producto 8", 441.9, cat1, "producto8.jpg"),
//						new Producto("producto 9", 91.3, cat2, "producto9.jpg")).flatMap(p -> {
//							p.setCreateAt(new Date());
//							return productoService.saveReactive(p);
//						}).doOnNext(p -> log.info(p.getNombre())))
//				.subscribe(p -> log.info(p.getNombre()));
	}
}
