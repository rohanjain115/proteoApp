package guru.springframework.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.UploadPartRequest;

import guru.springframework.util.StorageProperties;

@Service
@EnableConfigurationProperties(StorageProperties.class)
public class S3StorageService {

	private String awsAccessKeyID;
    private String awsSecretAccessKey;
    
    private AWSCredentials credentials;
    private AmazonS3 s3Client;
    
    public static final String SUFFIX = "/";
    
    public static final String DATABASE_FOLDER = "databases";
    
    public static final String EXPERIMENT_FOLDER = "experiments";
    
    public static final String PROJECT_FOLDER = "project";
    
    public static final String ACTIVE_FOLDER = "active";
    
    public static final String ARCHIVE_FOLDER = "archive";
    
    private List<PartETag> partETags = new ArrayList<PartETag>();
    private String existingBucketName;
    
    @Autowired
    public S3StorageService(StorageProperties properties) {
        this.awsAccessKeyID = properties.getAwsAccessKeyId();
        this.awsSecretAccessKey = properties.getAwsSecretAccessKey();
        this.existingBucketName = properties.getExistingBucketName();
        init();
    }
    
	public void init() {
		credentials = new BasicAWSCredentials(awsAccessKeyID, awsSecretAccessKey);
		s3Client = new AmazonS3Client(credentials);
	}
	
	public InputStream download(String fileName, String folderName){
		String bucketName = existingBucketName;
		String key = folderName +SUFFIX + fileName;	
		GetObjectRequest rangeObjectRequest = new GetObjectRequest(
				bucketName, key);
		S3Object objectPortion = s3Client.getObject(rangeObjectRequest);

		InputStream inputStream = objectPortion.getObjectContent();
		// Process the objectData stream.
		return inputStream;
	}

	public void upload(File file, String folderName) {
		String bucketName = existingBucketName;
		String key = folderName +SUFFIX + file.getName();
		
	    
	    long contentLength = file.length();
	    long partSize = 5 * 1024 * 1024; 
	    if(partSize > contentLength){
	    	uploadFile(file, bucketName, key);
	    }else{
	    	uploadFileInParts(file, contentLength, partSize, bucketName, key);
	    }
	}
	
	private void uploadFile(File file, String bucketName, String key){
		s3Client.putObject(new PutObjectRequest(bucketName, key, 
				file)
				.withCannedAcl(CannedAccessControlList.PublicRead));
	}
	private void uploadFileInParts(File file, long contentLength, long partSize, String bucketName, String key){
		InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, key);
	    InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);
		// Set part size to 5 MB.
		try {
	        // Step 2: Upload parts.
	        long filePosition = 0;
	        for (int i = 1; filePosition < contentLength; i++) {
	            // Last part can be less than 5 MB. Adjust part size.
	        	partSize = Math.min(partSize, (contentLength - filePosition));
	        	
	            // Create request to upload a part.
	            UploadPartRequest uploadRequest = new UploadPartRequest()
	                .withBucketName(bucketName).withKey(key)
	                .withUploadId(initResponse.getUploadId()).withPartNumber(i)
	                .withFileOffset(filePosition)
	                .withFile(file)
	                .withPartSize(partSize);

	            // Upload part and add response to our list.
	            partETags.add(s3Client.uploadPart(uploadRequest).getPartETag());

	            filePosition += partSize;
	        }

	        // Step 3: Complete.
	        CompleteMultipartUploadRequest compRequest = new 
	                    CompleteMultipartUploadRequest(bucketName, 
	                    								key, 
	                                                   initResponse.getUploadId(), 
	                                                   partETags);

	        s3Client.completeMultipartUpload(compRequest);
	    } catch (Exception e) {
	    	e.printStackTrace();
	        s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
	                  existingBucketName, key, initResponse.getUploadId()));
	    }	
	}
	
	public void deleteFile(String fileName, String folderName){
		String bucketName = existingBucketName;
		String key = folderName +SUFFIX + fileName;	
		s3Client.deleteObject(new DeleteObjectRequest(bucketName, key));
	}
	
	public void deleteFolder(String folderPath){
		String bucketName = existingBucketName;
		for (S3ObjectSummary file : s3Client.listObjects(bucketName, folderPath).getObjectSummaries()){
			s3Client.deleteObject(bucketName, file.getKey());
		}
		if(folderPath.contains("/")){
			String parentFolderPath = folderPath.substring(0, folderPath.lastIndexOf("/"));
			for (S3ObjectSummary file : s3Client.listObjects(bucketName, parentFolderPath).getObjectSummaries()){
				String key = file.getKey();
				key = key.endsWith(SUFFIX) ? key.substring(0,  key.lastIndexOf(SUFFIX)) : key;
				if(key.equals(folderPath)){
					s3Client.deleteObject(bucketName, file.getKey());
					break;
				}
			}
		}else{
			s3Client.deleteObject(bucketName, folderPath);
		}
	}
	
	public void copyFile(String fileName, String sourceFolderName, String destinationFolderName){
		String bucketName = existingBucketName;
		String key = sourceFolderName +SUFFIX + fileName;	
		String destinationKey = destinationFolderName + SUFFIX + fileName;
		s3Client.copyObject(new CopyObjectRequest(
        		bucketName, key, bucketName, destinationKey));
	}
	public void createFolder(String folderName) {
		if(!s3Client.doesObjectExist(existingBucketName, folderName)){
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(0);
			InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
			PutObjectRequest putObjectRequest = new PutObjectRequest(existingBucketName,
					folderName + SUFFIX, emptyContent, metadata);
			s3Client.putObject(putObjectRequest);
		}
	}
	
	public boolean doesFolderExists(String folderName){
		return s3Client.doesObjectExist(existingBucketName, folderName);
	}
}
