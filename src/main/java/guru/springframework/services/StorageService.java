package guru.springframework.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

	void store(MultipartFile file, String fileName, int chunks, int chunk);

    Path getDefaultFilePath();
    
    void delete(File file) throws Exception;
    
    Path createDirectory(String directoryName) throws IOException;
    
    void createDirectory(Path directoryPath) throws IOException;
    
    boolean fileExists(String fileOrDirectoryName);
    
    void delete(String fileOrDirectoryName)  throws IOException;
    
    void createFile(Path filePath, InputStream inputStream) throws IOException;
    
    Path createArchiveForDirectory(String directoryName) throws IOException;
}
