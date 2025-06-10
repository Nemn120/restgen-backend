package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class S3Service {

    private final AmazonS3 amazonS3;
    
    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public void uploadFolder(String uuid, File folder) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("The provided file is not a folder.");
        }
        try {
            uploadFolderRecursive(uuid, folder, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadFolderRecursive(String uuid, File folder, String parentPath) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                String s3Key = uuid + "/" + parentPath + file.getName();
                if (file.isDirectory()) {
                    uploadFolderRecursive(uuid, file, parentPath + file.getName() + "/");
                } else if (file.isFile()) {
                    PutObjectResult putObjectResult = amazonS3.putObject(new PutObjectRequest(bucketName, s3Key, file));
                    if (putObjectResult == null) {
                        throw new RuntimeException("Failed to upload file: " + file.getName());
                    }
                }
            }
        }
    }

    public S3Object downloadFile(String fileName) {
        return amazonS3.getObject(bucketName, fileName);
    }
}