package com.jdasilva.socialweb.webflux.productos.services;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;

public interface IUploadService {

	public Resource load(String filename);

	public String copy(FilePart file) throws IOException;

	public boolean delete(String fileName) throws IOException;

	public void deleteAll();

	public void init() throws IOException;

}
