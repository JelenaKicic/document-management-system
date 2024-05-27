package sigurnost.dms.controllers;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import sigurnost.dms.exceptions.BadRequestException;
import sigurnost.dms.exceptions.ForbiddenException;
import sigurnost.dms.models.dto.User;
import sigurnost.dms.models.dto.UserPermission;
import sigurnost.dms.models.enums.Role;
import sigurnost.dms.models.requests.UserPermissionRequest;
import sigurnost.dms.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserPermissionsController {

    private final UserService userService;
    public UserPermissionsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}/permissions")
    @ResponseStatus(HttpStatus.OK)
    public List<UserPermission> getUserPermissions(@PathVariable String id) {
        User currentUser = userService.getCurrentUser();
        if(!currentUser.getRole().equals(Role.system_admin)) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        if(!userService.checkIfUserIsClient(id)) {
            throw new BadRequestException();
        };

        return userService.getUserPermissions(id);
    }

    @PostMapping("/{id}/permissions")
    @ResponseStatus(HttpStatus.CREATED)
    public void addUserPermission(@PathVariable String id, @RequestBody UserPermissionRequest userPermissionRequest) {
        User currentUser = userService.getCurrentUser();
        if(!currentUser.getRole().equals(Role.system_admin)) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        if(!userService.checkIfUserIsClient(id)) {
            throw new ForbiddenException("You don't have permission to do this operation");
        };

        userService.addUserPermission(id, userPermissionRequest.getOperation());
    }

    @DeleteMapping("/permissions/{id}")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "*")
    public void deleteUserPermission(@PathVariable Integer id) {
        User currentUser = userService.getCurrentUser();
        if(!currentUser.getRole().equals(Role.system_admin)) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        userService.deleteUserPermission(id);
    }

}
