package sigurnost.dms.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sigurnost.dms.models.dto.Action;
import sigurnost.dms.models.entities.ActionEntity;
import sigurnost.dms.models.entities.PermissionEntity;
import sigurnost.dms.models.enums.PermissionOperation;
import sigurnost.dms.repositories.ActionRepository;
import sigurnost.dms.repositories.UserPermissionsRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActionService {
    @PersistenceContext
    private EntityManager entityManager;

    private final ModelMapper modelMapper;
    private final ActionRepository actionRepository;

    public ActionService(ActionRepository actionRepository, ModelMapper modelMapper) {
        this.actionRepository = actionRepository;
        this.modelMapper = modelMapper;
    }

    public List<Action> getActions() {
        return actionRepository.findAll().stream().map(e -> modelMapper.map(e, Action.class)).collect(Collectors.toList());
    }

    public void createAction(String operation, String username, String fileName) {
        ActionEntity actionEntity = new ActionEntity();
        actionEntity.setDocument(fileName);
        actionEntity.setTime(new Date(new java.util.Date().getTime()));
        actionEntity.setType(String.valueOf(operation));
        actionEntity.setUsername(username);

        actionEntity.setId(null);

        actionRepository.saveAndFlush(actionEntity);
    }
}
