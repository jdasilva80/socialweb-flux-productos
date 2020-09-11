package com.jdasilva.socialweb.webflux.productos.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jdasilva.socialweb.commons.models.productos.entity.Categoria;
import com.jdasilva.socialweb.commons.models.productos.entity.Producto;
import com.jdasilva.socialweb.webflux.productos.dao.CategoriaReactiveDao;
import com.jdasilva.socialweb.webflux.productos.dao.ProductosReactiveDao;
import com.jdasilva.socialweb.webflux.productos.dao.relational.CategoriaDao;
import com.jdasilva.socialweb.webflux.productos.dao.relational.ProductosDao;


@Service
public class ProductoServiceImpl implements IProductoService {
	
	private static final Logger log = LoggerFactory.getLogger(ProductoServiceImpl.class);

	@Autowired
	ProductosReactiveDao productosReactiveDao;

	@Autowired
	CategoriaReactiveDao categoriaReactiveDao;

	@Autowired
	@Qualifier("productosDao")
	ProductosDao productosDao;

	@Autowired
	@Qualifier("categoriaDao")
	CategoriaDao categoriaDao;
	

	@Override
	public Producto findById(Long id) {

		return productosDao.findById(id).orElse(null);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Producto> findAll() {

		return productosDao.findAll();
	}

	@Transactional(readOnly = true)
	@Override
	public List<Producto> findByNombre(String nombre) {

		log.info("$$$$$$$$$$$$$$$$$$  nombre " + nombre);

		return productosDao.findByNombreLikeIgnoreCase(nombre);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Producto> findAllUpperCase() {

		return productosDao.findAll().stream().map(p -> {
			p.setNombre(p.getNombre().toUpperCase());
			return p;
		}).collect(Collectors.toList());
	}

	@Transactional
	@Override
	public Producto save(Producto producto) {

		return productosDao.save(producto);
	}

	@Transactional
	@Override
	public void delete(Producto producto) {

		productosDao.delete(producto);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Categoria> findAllCategoria() {

		return categoriaDao.findAll();
	}

	@Transactional(readOnly = true)
	@Override
	public Categoria findCategoriaById(Long id) {

		return categoriaDao.findById(id).orElse(null);
	}

	@Transactional
	@Override
	public Categoria save(Categoria categoria) {

		return categoriaDao.save(categoria);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Producto> findAllCaseRepeat() {

		return productosDao.findAll();
	}

}
