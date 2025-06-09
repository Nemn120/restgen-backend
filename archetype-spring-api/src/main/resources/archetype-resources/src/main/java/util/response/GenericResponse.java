package ${groupId}.util.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericResponse<T> implements Serializable {

    private T data;
    private MessageResponse message;

    public static GenericResponse builder(){
        return new GenericResponse();
    }

    private GenericResponse(){
    }

    private GenericResponse(T data, MessageResponse message) {
        this.data = data;
        this.message = message;
    }

    public GenericResponse data(T data) {
        this.data = data;
        return this;
    }

    public GenericResponse message(MessageResponse message) {
        this.message = message;
        return this;
    }

    public GenericResponse message(String title,HttpStatus status){
        this.message = new MessageResponse(title,status);
        return this;
    }

    public GenericResponse<T> build(){
        return new GenericResponse(data,message);
    }

    @Override
    public String toString() {
        return "GenericResponse{" +
                "data=" + data +
                ", message=" + message.toString() +
                '}';
    }
}
