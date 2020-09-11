package com.jdasilva.socialweb.webflux.productos.services;


import java.util.List;

import com.jdasilva.socialweb.commons.models.document.Categoria;
import com.jdasilva.socialweb.commons.models.document.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductoReactiveService {

	public Flux<Producto> findAllReactive();

	public Flux<Producto> findAllUpperCaseReactive();	

	public Flux<Producto> findAllUpperCaseRepeatReactive();

	public Mono<Producto> findByIdReactive(String id);

	public Mono<Producto> saveReactive(Producto producto);

	public Mono<Categoria> saveReactive(Categoria categoria);	

	public Mono<Void> deleteReactive(Producto producto);

	public Flux<Categoria> findAllCategoriaReactive();	

	public Mono<Categoria> findCategoriaByIdReactive(String id);
	
	public List<Producto> findByNombre(String id);
	
}
