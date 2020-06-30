package com.jdasilva.webflux.app.services;

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
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService implements IUploadService {

	private static final Logger logger = LoggerFactory.getLogger(UploadService.class);

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
			logger.error(e.getMessage());
		}

		return recurso;
	}

	@Override
	public String copy(MultipartFile multipartFile) throws IOException {

		String uniqueFileName = null;

		if (multipartFile != null && !multipartFile.isEmpty()) {

			uniqueFileName = UUID.randomUUID().toString().concat("_").concat(multipartFile.getOriginalFilename());
			Path rootPath = getAbsolutePath(uniqueFileName);
			// Files.write(completePath, file.getBytes());
			Files.copy(multipartFile.getInputStream(), rootPath);
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
