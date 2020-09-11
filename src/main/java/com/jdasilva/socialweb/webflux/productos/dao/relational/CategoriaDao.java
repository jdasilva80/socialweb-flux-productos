package com.jdasilva.socialweb.webflux.productos.dao.relational;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jdasilva.socialweb.commons.models.productos.entity.Categoria;

@Repository("categoriaDao")
public interface CategoriaDao extends JpaRepository<Categoria, Long> {

}
