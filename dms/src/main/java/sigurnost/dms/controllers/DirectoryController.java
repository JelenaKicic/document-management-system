package sigurnost.dms.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sigurnost.dms.exceptions.BadRequestException;
import sigurnost.dms.exceptions.ForbiddenException;
import sigurnost.dms.models.dto.FileNode;
import sigurnost.dms.models.dto.User;
import sigurnost.dms.models.enums.Role;
import sigurnost.dms.models.requests.DirectoryRequest;
import sigurnost.dms.services.ActionService;
import sigurnost.dms.services.DirectoryService;
import sigurnost.dms.services.EmailService;
import sigurnost.dms.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;

@RestController
@RequestMapping("/directories")
@CrossOrigin(origins = "*")
public class DirectoryController {

    private final DirectoryService directoryService;
    private final EmailService emailService;
    private final UserService userService;
    private final ActionService actionService;
    public DirectoryController(UserService userService, DirectoryService directoryService, ActionService actionService, EmailService emailService) {
        this.userService = userService;
        this.directoryService = directoryService;
        this.actionService = actionService;
        this.emailService = emailService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public FileNode getFiles(@RequestParam(defaultValue = "false") boolean dir, HttpServletRequest request) {
        User currentUser = userService.getCurrentUser();
        if((currentUser.getRole().equals(Role.client) && (!request.getRemoteAddr().equals(currentUser.getIp())) && !request.getRemoteAddr().equals("0:0:0:0:0:0:0:1"))) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        return directoryService.getFileSystemStructure(currentUser.getRootDir(), dir);
    }


    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDirectory(@RequestHeader("path") String path, HttpServletRequest request) {
        User currentUser = userService.getCurrentUser();
        if(!currentUser.getRole().equals(Role.documents_admin)) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        File directory = new File(path);

        if (!directory.exists() || !directory.isDirectory()) {
            throw new BadRequestException("Select directory");
        }

        try {

                Files.walk(Paths.get(path))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);

            actionService.createAction("DELETE_DIRECTORY", currentUser.getUsername(), directory.getName());

            emailService.sendEmail("Deletes Directory", currentUser.getEmail(), "User " + currentUser.getUsername() + " just deleted " + directory.getName() + " directory.");
        } catch (IOException e) {
            throw new BadRequestException();
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    public void createDirectory(@RequestBody() DirectoryRequest data, HttpServletRequest request) {
        User currentUser = userService.getCurrentUser();
        if(!currentUser.getRole().equals(Role.documents_admin)) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        File parent = new File(data.getPath());

        if (!parent.exists() || !parent.isDirectory()) {
            throw new BadRequestException("Select directory");
        }

        new File(data.getPath() + File.separator + data.getName()).mkdir();

        actionService.createAction("CREATE_DIRECTORY", currentUser.getUsername(), data.getName());
    }
}
