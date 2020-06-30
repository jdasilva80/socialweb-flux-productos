package com.jdasilva.webflux.app.services;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadService {

	public Resource load(String filename);

	public String copy(MultipartFile multipartFile) throws IOException;

	public boolean delete(String fileName) throws IOException;

	public void deleteAll();

	public void init() throws IOException;

}
