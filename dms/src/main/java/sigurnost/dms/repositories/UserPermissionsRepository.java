package sigurnost.dms.repositories;

import jdk.dynalink.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import sigurnost.dms.models.entities.PermissionEntity;
import sigurnost.dms.models.enums.PermissionOperation;

import java.util.List;

public interface UserPermissionsRepository extends JpaRepository<PermissionEntity, Integer> {
    public void deleteById(Integer id);
    public List<PermissionEntity> findByUserId(String id);
    public List<PermissionEntity> findByUserIdAndOperation(String id, String operation);
}
