package ${groupId}.util.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
@Setter
public class CustomPage<T> {

    private List<T> data;
    private int actualPage;
    private int pageSize;
    private String sortField;
    private int totalPage;
    private long totalElements;
    private Sort.Direction sortDirection;

    private MessageResponse message;

    public CustomPage(List<T> content, Pageable pageable, long totalElements, int totalPage, String sortField, Sort.Direction direction) {
        this.data = content;
        this.actualPage = pageable.getPageNumber()+1;
        this.sortField = sortField;
        this.pageSize = pageable.getPageSize();
        this.totalElements = totalElements;
        this.sortDirection = direction;
        this.totalPage = totalPage;
    }
}
