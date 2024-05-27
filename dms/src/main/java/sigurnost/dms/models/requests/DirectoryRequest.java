package sigurnost.dms.models.requests;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DirectoryRequest extends FileRequest {
    String name;
}
