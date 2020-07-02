package com.jdasilva.socialweb.webflux.productos.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jdasilva.socialweb.commons.models.document.Categoria;
import com.jdasilva.socialweb.commons.models.document.Producto;
import com.jdasilva.socialweb.webflux.productos.dao.CategoriaDao;
import com.jdasilva.socialweb.webflux.productos.dao.CategoriaReactiveDao;
import com.jdasilva.socialweb.webflux.productos.dao.ProductosDao;
import com.jdasilva.socialweb.webflux.productos.dao.ProductosReactiveDao;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImpl implements IProductoService {

	@Autowired
	ProductosReactiveDao productosReactiveDao;

	@Autowired
	CategoriaReactiveDao categoriaReactiveDao;

	@Autowired
	ProductosDao productosDao;

	@Autowired
	CategoriaDao categoriaDao;

	@Transactional(readOnly = true)
	@Override
	public Flux<Producto> findAllReactive() {

		return productosReactiveDao.findAll();
	}

	@Transactional(readOnly = true)
	@Override
	public Flux<Producto> findAllUpperCaseReactive() {

		return productosReactiveDao.findAll().map(p -> {
			p.setNombre(p.getNombre().toUpperCase());
			return p;
		});
	}

	@Transactional(readOnly = true)
	@Override
	public Flux<Producto> findAllUpperCaseRepeatReactive() {

		return productosReactiveDao.findAll().map(p -> {
			p.setNombre(p.getNombre().toUpperCase());
			return p;
		}).repeat(5000);
	}

	@Transactional(readOnly = true)
	@Override
	public Mono<Producto> findByIdReactive(String id) {

		return productosReactiveDao.findById(id);
	}

	@Override
	public Producto findById(String id) {

		return productosDao.findById(id).orElse(null);
	}

	@Transactional
	@Override
	public Mono<Producto> saveReactive(Producto producto) {

		return productosReactiveDao.save(producto);
	}

	@Transactional
	@Override
	public Mono<Void> deleteReactive(Producto producto) {

		return productosReactiveDao.delete(producto);
	}

	@Transactional(readOnly = true)
	@Override
	public Flux<Categoria> findAllCategoriaReactive() {

		return categoriaReactiveDao.findAll();
	}

	@Transactional(readOnly = true)
	@Override
	public Mono<Categoria> findCategoriaByIdReactive(String id) {

		return categoriaReactiveDao.findById(id);
	}

	@Transactional
	@Override
	public Mono<Categoria> saveReactive(Categoria categoria) {

		return categoriaReactiveDao.save(categoria);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Producto> findAll() {

		return productosDao.findAll();
	}

	@Transactional(readOnly = true)
	@Override
	public List<Producto> findByNombre(String nombre) {
		
		return productosDao.findByNombreLikeIgnoreCase(nombre);
	}

}
