package com.jdasilva.socialweb.webflux.productos.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jdasilva.socialweb.commons.models.document.Categoria;


public interface CategoriaDao extends MongoRepository<Categoria, String> {

}
