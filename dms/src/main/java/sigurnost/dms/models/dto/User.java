package sigurnost.dms.models.dto;

import lombok.Getter;
import lombok.Setter;
import sigurnost.dms.models.enums.Role;

@Getter
@Setter
public class User {
    String id;
    String username;
    String email;
    String ip;
    String rootDir;
    Role role;
}
