package ${groupId}.util.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Hidden
@JsonInclude(Include.NON_NULL)
public class MessageResponse {

    private HttpStatus code;
    private String title;
    private String date;
    private String description;


    public MessageResponse(String title, String description, HttpStatus code) {
       this(title,code);
        this.date = getDate();
    }

    public MessageResponse(String title, HttpStatus code) {
        this.title = title;
        this.code = code;
        this.date = getDate();
    }

    public String getDate() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }

}
