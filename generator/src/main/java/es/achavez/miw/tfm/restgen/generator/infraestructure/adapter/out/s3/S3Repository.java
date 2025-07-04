package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.achavez.miw.tfm.restgen.generator.domain.GithubRepository;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.GitHubUploadDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class S3Repository {

    private static final Logger logger = LogManager.getLogger(S3Repository.class);


    private final AmazonS3 amazonS3;
    
    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Repository(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public void uploadFolder(String uuid, File folder) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("The provided file is not a folder.");
        }
        long startTime = System.currentTimeMillis();
        try {
            uploadFolderRecursive(uuid, folder, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        logger.info("Tiempo total de subida a S3: " + (endTime - startTime) + " ms");
    }

    private void uploadFolderRecursive(String uuid, File folder, String parentPath) {
        File[] files = folder.listFiles();
        if (files != null) {
            Stream.of(files).parallel().forEach(file -> {
                String s3Key = uuid + "/" + parentPath + file.getName();
                if (file.isDirectory()) {
                    uploadFolderRecursive(uuid, file, parentPath + file.getName() + "/");
                } else if (file.isFile()) {
                    CompletableFuture.runAsync(() -> {
                        PutObjectResult putObjectResult = amazonS3.putObject(new PutObjectRequest(bucketName, s3Key, file));
                        if (putObjectResult == null) {
                            throw new RuntimeException("Failed to upload file: " + file.getName());
                        }
                    }).join();
                }
            });
        }
    }


    public void uploadFolderAsZip(String uuid, File folder)  {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("The provided file is not a folder.");
        }
        File zipFile = new File(System.getProperty("java.io.tmpdir"), uuid + ".zip");
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zipFolder(folder, folder.getName(), zos);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long startTime = System.currentTimeMillis();
        PutObjectResult putObjectResult = amazonS3.putObject(new PutObjectRequest(bucketName, uuid + ".zip", zipFile));
        if (putObjectResult == null) {
            throw new RuntimeException("Failed to upload ZIP file: " + zipFile.getName());
        }
        long endTime = System.currentTimeMillis();
        logger.info("Tiempo total de subida del ZIP a S3: " + (endTime - startTime) + " ms");
        zipFile.delete();
    }

    private void zipFolder(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                zipFolder(file, parentFolder + "/" + file.getName(), zos);
            } else {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(parentFolder + "/" + file.getName());
                    zos.putNextEntry(zipEntry);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
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

    public GithubRepository uploadGithub(String urlRepository, GitHubUploadDto uploadGithub) throws IOException {
        File localFolder = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        if (!localFolder.exists()) {
            localFolder.mkdirs();
        }
        List<File> files = this.downloadFolder(urlRepository);

        for (File file : files) {
            String relativePath = file.getPath().substring(file.getPath().indexOf(urlRepository) + urlRepository.length());
            File destinationFile = new File(localFolder, relativePath);

            if (!destinationFile.getParentFile().exists()) {
                destinationFile.getParentFile().mkdirs();
            }
            Files.copy(file.toPath(), destinationFile.toPath());
        }

        String repoUrl;
        try {
            if (repositoryExists(uploadGithub)) {
                repoUrl = getRepositoryUrl(uploadGithub);
                commitChangesToGitHub(localFolder, repoUrl, uploadGithub.githubToken());
            } else {
                repoUrl = createGitHubRepository(uploadGithub);
                pushToGitHub(localFolder, repoUrl, uploadGithub.githubToken());
            }
        } catch (GitAPIException | URISyntaxException e) {
            throw new RuntimeException(e);
        } finally {
            deleteDirectory(localFolder);
        }

        GithubRepository repository = new GithubRepository();
        repository.setUrl(repoUrl);
        repository.setDescription(uploadGithub.description());
        repository.setIsPrivate(uploadGithub.isPrivate());
        repository.setName(uploadGithub.projectName());
        return repository;
    }

    private boolean repositoryExists(GitHubUploadDto uploadGithub) throws IOException {
        URL url = new URL("https://api.github.com/repos/" + "/" + uploadGithub.projectName());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "token " + uploadGithub.githubToken());
        return connection.getResponseCode() == 200;
    }

    private void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }

    private String getRepositoryUrl(GitHubUploadDto uploadGithub) {
        //return "https://github.com/" + uploadGithub.username() + "/" + uploadGithub.projectName() + ".git";
        return "";
    }

    private void commitChangesToGitHub(File localFolder, String repoUrl, String githubToken) throws GitAPIException, URISyntaxException {
        try (Git git = Git.open(localFolder)) {
            git.add().addFilepattern(".").call();
            git.commit().setMessage("Update project").call();
            git.push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubToken, ""))
                    .setRemote("origin")
                    .call();
        } catch (IOException e) {
            throw new RuntimeException("Error al abrir el repositorio local", e);
        }
    }

    private String createGitHubRepository(GitHubUploadDto dto) throws IOException {
        URL url = new URL("https://api.github.com/user/repos");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "token " + dto.githubToken());
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", dto.projectName());
        requestBody.put("description", dto.description());
        requestBody.put("private", dto.isPrivate());

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        connection.getOutputStream().write(jsonBody.getBytes());
        if (connection.getResponseCode() != 201) {
            throw new IOException("Error al crear el repositorio en GitHub: " + connection.getResponseMessage());
        }

        Map<String, Object> response = objectMapper.readValue(connection.getInputStream(), Map.class);
        return (String) response.get("clone_url");
    }

    private void pushToGitHub(File localFolder, String repoUrl, String githubToken) throws GitAPIException, URISyntaxException {
        try (Git git = Git.init().setDirectory(localFolder).call()) {
            git.add().addFilepattern(".").call();
            git.commit().setMessage("Initial commit").call();
            git.remoteAdd()
                    .setName("origin")
                    .setUri(new URIish(repoUrl))
                    .call();
            git.push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubToken, ""))
                    .setRemote("origin")
                    .call();
        }
    }

    public InputStream downloadZip(String uuid) throws IOException {
        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, uuid + ".zip"));
        InputStream inputStream = s3Object.getObjectContent();
        if (inputStream == null) {
            throw new FileNotFoundException("File not found in S3: " + uuid + ".zip");
        }
        return inputStream;
    }
}