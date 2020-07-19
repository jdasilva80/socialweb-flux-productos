package com.jdasilva.socialweb.webflux.productos.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

@Service
public class UploadService implements IUploadService {

	private static final Logger log = LoggerFactory.getLogger(UploadService.class);

	private static final String UPLOADS = "uploads";

	@Override
	public Resource load(String filename) {

		Path rootPath = getAbsolutePath(filename);
		Resource recurso = null;

		try {
			recurso = new UrlResource(rootPath.toUri());

			if (!recurso.exists() && !recurso.isReadable()) {
				throw new RuntimeException("no se puede cargar la foto : ".concat(filename));
			}
		} catch (MalformedURLException e) {
			log.error(e.getMessage());
		}
		
		log.info("se asigna ha obtenido el recurso: " + recurso.getFilename());

		return recurso;
	}

	@Override
	public String copy(FilePart file) throws IOException {

		String uniqueFileName = null;

		if (file != null) {

			uniqueFileName = UUID.randomUUID().toString().concat("_").concat(file.filename());
			log.info("se asigna el nombre: " + uniqueFileName);
			Path rootPath = getAbsolutePath(uniqueFileName);
			log.info("rootpath: " + rootPath.toString());
			// Files.write(completePath, file.getBytes());
			// Files.copy(multipartFile.getInputStream(), rootPath);
			file.transferTo(rootPath);
			log.info("se copia el archivo ");
		} else {
			log.info("el partFile es NULL");
		}
		return uniqueFileName;
	}

	@Override
	public boolean delete(String fileName) throws IOException {

		return Files.deleteIfExists(getAbsolutePath(fileName));

	}

	public Path getAbsolutePath(String filename) {

		return Paths.get(UPLOADS).resolve(filename).toAbsolutePath();
	}

	@Override
	public void deleteAll() {

		FileSystemUtils.deleteRecursively(Paths.get(UPLOADS).toFile());

	}

	@Override
	public void init() throws IOException {

		Files.createDirectory(Paths.get(UPLOADS));

	}

}
