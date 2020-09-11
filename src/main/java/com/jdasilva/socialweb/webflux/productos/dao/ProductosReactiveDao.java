package com.jdasilva.socialweb.webflux.productos.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.jdasilva.socialweb.commons.models.document.Producto;

public interface ProductosReactiveDao extends ReactiveMongoRepository<Producto, String> {
	
	public List<Producto> findByNombreLikeIgnoreCase(String nombre);

}
