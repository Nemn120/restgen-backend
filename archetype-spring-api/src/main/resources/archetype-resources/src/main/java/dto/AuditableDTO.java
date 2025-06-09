package ${groupId}.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditableDTO<ID> {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private ID id;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String createdAt;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String updatedAt;

}
