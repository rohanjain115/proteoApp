package guru.springframework.controllers;

import java.io.File;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import guru.springframework.services.S3StorageService;
import guru.springframework.services.StorageService;

@Controller
@RequestMapping("/fileUpload")
public class FileUploadController {

    private final StorageService storageService;
    
    @Autowired
    private S3StorageService s3StorageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<Void> handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("filename") String fileName, @RequestParam(required=false, defaultValue="-1") int chunks, @RequestParam(required=false, defaultValue="-1") int chunk, @RequestParam(name="foldername") String folderName, Principal principal) throws Exception {
    	String filePath = storageService.getDefaultFilePath().toString() + File.separator + fileName;
    	if(chunk == 0){
    		storageService.delete(new File(filePath));
    	}
        storageService.store(file, fileName, chunks, chunk);
        if(chunk == (chunks - 1)){
        	File fileSystemFile = new File(storageService.getDefaultFilePath().toString() + File.separator + fileName);
        	if(folderName.contains("/")){
        		String folderNameArr []= folderName.split("/");
        		for(int i=0;i<folderNameArr.length; i++){
        			if(i != 0){
        				String folderPath = "";
        				for(int j=0; j<i;j++){
        					folderPath = folderPath + "/" + folderNameArr[j];
        				}
        				if(folderPath.startsWith("/")){
        					folderPath = folderPath.substring(1);
        				}
        				s3StorageService.createFolder(folderPath);
        			}
        		}
        		
        	}else{
        		s3StorageService.createFolder(folderName);
        	}
        	
            s3StorageService.upload(fileSystemFile, folderName);
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
