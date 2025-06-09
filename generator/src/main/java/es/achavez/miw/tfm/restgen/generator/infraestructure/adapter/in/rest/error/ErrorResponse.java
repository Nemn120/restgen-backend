package es.achavez.miw.tfm.restgen.generator.infraestructure.adapter.in.rest.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String path;
    private Map<String, String> validationErrors;

    public ErrorResponse(int status, String error, Map<String, String> validationErrors) {
        this.status = status;
        this.error = error;
        this.validationErrors = validationErrors;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String error) {
        this(status, error, null);
    }
}