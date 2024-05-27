package sigurnost.dms.models.dto;

import lombok.Getter;
import lombok.Setter;
import sigurnost.dms.models.enums.PermissionOperation;

@Getter
@Setter
public class UserPermission {
    int id;
    String userId;
    PermissionOperation operation;
}
