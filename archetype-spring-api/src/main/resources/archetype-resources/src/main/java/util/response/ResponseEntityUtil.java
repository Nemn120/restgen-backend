package ${groupId}.util.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseEntityUtil {

    public static <T> ResponseEntity<GenericResponse<T>> createResponse(T data, String message, HttpStatus httpStatus) {
        GenericResponse response = GenericResponse.builder()
                .data(data)
                .message(message, httpStatus)
                .build();
        return ResponseEntity.status(httpStatus).body(response);
    }

    public static ResponseEntity createEmptyResponse(String message, HttpStatus httpStatus) {
        GenericResponse response = GenericResponse.<Void>builder()
                .message(message, httpStatus)
                .build();
        return ResponseEntity.status(httpStatus).body(response);
    }

    public static <T> ResponseEntity<CustomPage<T>> createCustomPageResponse(CustomPage<T> customPage, String message, HttpStatus httpStatus) {
        MessageResponse messageResponse = new MessageResponse(message, httpStatus);
        customPage.setMessage(messageResponse);
        return ResponseEntity.status(httpStatus).body(customPage);
    }

}
