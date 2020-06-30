package com.jdasilva.webflux.app.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.jdasilva.socialweb.commons.models.document.Producto;

public interface ProductosReactiveDao extends ReactiveMongoRepository<Producto, String> {

}
