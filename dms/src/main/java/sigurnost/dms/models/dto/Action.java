package sigurnost.dms.models.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Getter
@Setter
public class Action {
    private Integer id;
    private Date time;
    private String type;
    private String username;
    private String document;
}
