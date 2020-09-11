package com.jdasilva.socialweb.webflux.productos.services;

import java.util.List;

import com.jdasilva.socialweb.commons.models.productos.entity.Categoria;
import com.jdasilva.socialweb.commons.models.productos.entity.Producto;


public interface IProductoService {
	
	public List<Producto> findAll();
	
	public List<Producto> findAllUpperCase();
	
	public Producto findById(Long id);
	
	public List<Producto> findByNombre(String id);
	
	public Producto save(Producto producto);
	
	public Categoria save(Categoria categoria);
	
	public void delete(Producto producto);
	
	public List<Categoria> findAllCategoria();
	
	public Categoria findCategoriaById(Long id);
	
	public List<Producto> findAllCaseRepeat();

}
