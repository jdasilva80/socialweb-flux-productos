package com.jdasilva.socialweb.webflux.productos.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jdasilva.socialweb.commons.models.document.Producto;

public interface ProductosDao extends MongoRepository<Producto, String> {

	public List<Producto> findByNombreLikeIgnoreCase(String nombre);
	
}
