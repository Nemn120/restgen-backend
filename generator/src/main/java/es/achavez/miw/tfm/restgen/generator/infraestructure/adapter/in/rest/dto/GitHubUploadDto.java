package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto;


public record GitHubUploadDto(
        String projectName,
        String description,
        Boolean isPrivate,
        String githubToken) {

}