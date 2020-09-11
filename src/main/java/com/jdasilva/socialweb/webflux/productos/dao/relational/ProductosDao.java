package com.jdasilva.socialweb.webflux.productos.dao.relational;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jdasilva.socialweb.commons.models.productos.entity.Producto;

@Repository("productosDao")
public interface ProductosDao extends JpaRepository<Producto, Long> {
	
	public List<Producto> findByNombreLikeIgnoreCase(String nombre);
	
}
