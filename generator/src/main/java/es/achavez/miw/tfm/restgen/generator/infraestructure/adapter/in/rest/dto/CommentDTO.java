package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDTO {
    private String content;
    private LocalDateTime createdAt;
}