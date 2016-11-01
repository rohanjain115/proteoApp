package guru.springframework.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.BufferedInputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import guru.springframework.util.StorageException;

@Service
public class FileSystemStorageService implements StorageService {

	private final Path rootLocation = Paths.get(System.getProperty("java.io.tmpdir"));

	@Override
	public void store(MultipartFile file, String fileName, int chunks, int chunk) {
		OutputStream outputStream = null;
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + fileName);
			}

			Path filePath = this.rootLocation.resolve(fileName);

			outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			byte[] bytes = new byte[file.getInputStream().available()];
			file.getInputStream().read(bytes);
			outputStream.write(bytes, 0, bytes.length);
			System.out.println(filePath.toFile() + " " + chunk);
		} catch (IOException e) {
			throw new StorageException("Failed to store file " + fileName, e);
		} finally {
			try {
				outputStream.close();
			} catch (IOException io) {
				io.printStackTrace();
			}

		}
	}

	@Override
	public Path getDefaultFilePath() {
		return rootLocation;
	}

	@Override
	public void delete(File file) throws IOException {
		if (file.exists() && file.isFile()) {
			Files.delete(file.toPath());
		}
	}

	@Override
	public Path createDirectory(String directoryName) throws IOException {
		Path filePath = this.rootLocation.resolve(directoryName);
		return Files.createDirectories(filePath);
	}
	
	@Override
	public void createDirectory(Path directoryPath) throws IOException {
		Files.createDirectories(directoryPath);
	}
	@Override
	public boolean fileExists(String fileOrDirectoryName) {
		return Files.exists(this.rootLocation.resolve(fileOrDirectoryName));
	}

	@Override
	public void delete(String fileOrDirectoryName) throws IOException {
		Path filePath = this.rootLocation.resolve(fileOrDirectoryName);
		if (Files.isDirectory(filePath)) {
			Files.walkFileTree(filePath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					if (exc == null) {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					} else {
						throw exc;
					}
				}
			});
		} else {
			Files.delete(filePath);
		}
	}

	@Override
	public void createFile(Path filePath, InputStream inputStream) throws IOException {
		OutputStream outputStream = null;
		try {
			outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			//byte[] bytes = new byte[inputStream.available()];
			//inputStream.read(bytes);
			//outputStream.write(bytes, 0, bytes.length);
			InputStream in = new BufferedInputStream(inputStream);

 

			byte[] bytes = new byte[1000];

			while (in.read(bytes) != -1){ 
	
			outputStream.write(bytes);

			outputStream.flush();
			bytes = new byte[1000];
			}
		} finally {
			inputStream.close();
			outputStream.close();
		}
	}

	@Override
	public Path createArchiveForDirectory(String directoryName) throws IOException {
		Path filePath = this.rootLocation.resolve(directoryName);
		if (Files.isDirectory(filePath)) {
			Path zipFilePath = this.rootLocation.resolve(directoryName + ".zip");
			try (
		            FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
		            ZipOutputStream zos = new ZipOutputStream(fos)
		    ) {
		        Files.walkFileTree(filePath, new SimpleFileVisitor<Path>() {
		            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		                zos.putNextEntry(new ZipEntry(filePath.relativize(file).toString()));
		                Files.copy(file, zos);
		                zos.closeEntry();
		                return FileVisitResult.CONTINUE;
		            }

		            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		                zos.putNextEntry(new ZipEntry(filePath.relativize(dir).toString() + "/"));
		                zos.closeEntry();
		                return FileVisitResult.CONTINUE;
		            }
		        });
		    }
			
			return zipFilePath;
		}

		return null;
	}
}
