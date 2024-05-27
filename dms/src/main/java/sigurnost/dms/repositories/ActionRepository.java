package sigurnost.dms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sigurnost.dms.models.entities.ActionEntity;

public interface ActionRepository extends JpaRepository<ActionEntity, Integer> {
}
