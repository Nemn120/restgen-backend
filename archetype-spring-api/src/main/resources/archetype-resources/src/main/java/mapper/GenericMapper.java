package ${groupId}.mapper;

import org.apache.logging.log4j.util.Strings;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public interface GenericMapper<E, D> {

    E toEntity(D dto);
    D toDto(E entity);

    default String parseToStringWithFormat(LocalDateTime dateTime) {
        if(dateTime != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return dateTime.format(formatter);
        }
        return Strings.EMPTY;
    }

    default LocalDateTime parseToLocalDateTime(String formattedDateTime) {
        if(!Strings.isEmpty(formattedDateTime)){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return LocalDateTime.parse(formattedDateTime, formatter);
        }
        return null;
    }

}


