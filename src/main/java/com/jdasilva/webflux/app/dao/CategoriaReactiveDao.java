package com.jdasilva.webflux.app.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.jdasilva.socialweb.commons.models.document.Categoria;


public interface CategoriaReactiveDao extends ReactiveMongoRepository<Categoria, String> {

}
