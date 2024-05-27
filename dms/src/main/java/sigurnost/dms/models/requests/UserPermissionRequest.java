package sigurnost.dms.models.requests;

import lombok.Getter;
import lombok.Setter;
import sigurnost.dms.models.enums.PermissionOperation;

@Setter
@Getter
public class UserPermissionRequest {
    PermissionOperation operation;
}
