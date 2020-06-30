package com.jdasilva.webflux.app.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jdasilva.socialweb.commons.models.document.Categoria;


public interface CategoriaDao extends MongoRepository<Categoria, String> {

}
