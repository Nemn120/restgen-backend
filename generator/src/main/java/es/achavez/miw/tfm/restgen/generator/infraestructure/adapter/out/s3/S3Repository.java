package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.out.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.achavez.miw.tfm.restgen.generator.domain.GithubRepository;
import es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto.GitHubUploadDto;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

@Slf4j
@Service
public class S3Repository {

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
        //URL url = new URL("https://api.github.com/repos/" + uploadGithub.username() + "/" + uploadGithub.projectName());
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
}