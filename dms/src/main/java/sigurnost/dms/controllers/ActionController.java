package sigurnost.dms.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sigurnost.dms.exceptions.BadRequestException;
import sigurnost.dms.exceptions.ForbiddenException;
import sigurnost.dms.models.dto.Action;
import sigurnost.dms.models.dto.User;
import sigurnost.dms.models.dto.UserPermission;
import sigurnost.dms.models.enums.Role;
import sigurnost.dms.services.ActionService;
import sigurnost.dms.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/actions")
@CrossOrigin(origins = "*")
public class ActionController {

    private final ActionService actionService;
    private final UserService userService;

    public ActionController(ActionService actionService, UserService userService) {
        this.actionService = actionService;
        this.userService = userService;
    }


    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Action> getActions() {
        User currentUser = userService.getCurrentUser();
        if(!currentUser.getRole().equals(Role.system_admin)) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        return actionService.getActions();
    }
}
