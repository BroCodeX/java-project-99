package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class StatusDTO {
    private Long id;
    private String name;
    private String slug;
    private LocalDate createdAt;
}
