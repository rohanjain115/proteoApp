package guru.springframework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.FileSystemResource;

public class DeleteAfterReadeFileSystemResource extends FileSystemResource {
    public DeleteAfterReadeFileSystemResource(File file) {
        super(file);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new DeleteOnCloseFileInputStream(super.getFile());
    }

    private static final class DeleteOnCloseFileInputStream extends FileInputStream {

        private File file;
        DeleteOnCloseFileInputStream(File file) throws FileNotFoundException    {
            super(file);
            this.file = file;
        }

        @Override
        public void close() throws IOException {
            super.close();
            file.delete();
        }
    }
}	