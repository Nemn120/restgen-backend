package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public List<File> downloadFolder(String folderName) throws IOException {
        ObjectListing objectListing = amazonS3.listObjects(bucketName, folderName);
        List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        List<File> downloadedFiles = new ArrayList<>();

        for (S3ObjectSummary summary : objectSummaries) {
            String key = summary.getKey();
            File tempFile = new File(System.getProperty("java.io.tmpdir"), key);
            tempFile.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                amazonS3.getObject(bucketName, key).getObjectContent().transferTo(fos);
            }
            downloadedFiles.add(tempFile);
        }
        return downloadedFiles;
    }
}