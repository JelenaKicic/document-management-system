package sigurnost.dms.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sigurnost.dms.exceptions.BadRequestException;
import sigurnost.dms.exceptions.ForbiddenException;
import sigurnost.dms.models.dto.User;
import sigurnost.dms.models.dto.UserPermission;
import sigurnost.dms.models.enums.PermissionOperation;
import sigurnost.dms.models.enums.Role;
import sigurnost.dms.models.requests.FileRequest;
import org.apache.commons.io.IOUtils;
import sigurnost.dms.models.requests.MoveFileRequest;
import sigurnost.dms.services.ActionService;
import sigurnost.dms.services.DirectoryService;
import sigurnost.dms.services.EmailService;
import sigurnost.dms.services.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "*")
public class FileController {

    private final UserService userService;
    private final ActionService actionService;
    private final EmailService emailService;
    public FileController(UserService userService, ActionService actionService, EmailService emailService) {
        this.userService = userService;
        this.actionService = actionService;
        this.emailService = emailService;
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadFile(@RequestHeader("path") String path, @RequestParam("file")MultipartFile multipartFile, HttpServletRequest request) {
        User currentUser = userService.getCurrentUser();

        if(currentUser.getRole().equals(Role.system_admin)) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        if((currentUser.getRole().equals(Role.client) && (!request.getRemoteAddr().equals(currentUser.getIp())) && !request.getRemoteAddr().equals("0:0:0:0:0:0:0:1")) || currentUser.getRole().equals(Role.system_admin)) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        if(currentUser.getRole().equals(Role.client)) {
            userService.checkClientPermissions(currentUser, PermissionOperation.CREATE);
        }

        File directory = new File(path);

        if (!directory.getPath().startsWith(currentUser.getRootDir())) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        if (!directory.exists() || !directory.isDirectory()) {
            throw new BadRequestException("Select directory");
        }

        File targetFile = new File(path + File.separator + multipartFile.getOriginalFilename());
        try(OutputStream outputStream = new FileOutputStream(targetFile)) {
            IOUtils.copy(multipartFile.getInputStream(), outputStream);
        } catch (IOException e) {
            throw new BadRequestException();
        }

        actionService.createAction(String.valueOf(PermissionOperation.CREATE), currentUser.getUsername(), multipartFile.getOriginalFilename());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/download")
    public @ResponseBody byte[] downloadFile(@PathParam("filePath") String filePath, HttpServletRequest request) {
        User currentUser = userService.getCurrentUser();
        if((currentUser.getRole().equals(Role.client) && (!request.getRemoteAddr().equals(currentUser.getIp())) && !request.getRemoteAddr().equals("0:0:0:0:0:0:0:1"))) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        if(currentUser.getRole().equals(Role.client)) {
            userService.checkClientPermissions(currentUser, PermissionOperation.READ);
        }

        File file = new File(filePath);

        if (!file.getPath().startsWith(currentUser.getRootDir())) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        if (!file.exists() || file.isDirectory()) {
            throw new BadRequestException("Select file");
        }

        try {
            InputStream inputStream = new FileInputStream(file);

            actionService.createAction(String.valueOf(PermissionOperation.READ), currentUser.getUsername(), file.getName());

            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new BadRequestException();
        }
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(@RequestParam() String filePath, HttpServletRequest request) {
        User currentUser = userService.getCurrentUser();

        if(currentUser.getRole().equals(Role.system_admin)) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        if((currentUser.getRole().equals(Role.client) && (!request.getRemoteAddr().equals(currentUser.getIp())) && !request.getRemoteAddr().equals("0:0:0:0:0:0:0:1")) || currentUser.getRole().equals(Role.system_admin)) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        if(currentUser.getRole().equals(Role.client)) {
            userService.checkClientPermissions(currentUser, PermissionOperation.DELETE);
        }

        File file = new File(filePath);

        if (!file.getPath().startsWith(currentUser.getRootDir())) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        if (!file.exists() || file.isDirectory()) {
            throw new BadRequestException("Select file");
        }

        actionService.createAction(String.valueOf(PermissionOperation.DELETE), currentUser.getUsername(), file.getName());
        emailService.sendEmail("Deletes Directory", currentUser.getEmail(), "User " + currentUser.getUsername() + " just deleted " + file.getName() + " file.");

        file.delete();
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateFile(@RequestHeader("path") String path, @RequestParam("file")MultipartFile multipartFile, HttpServletRequest request) {
        User currentUser = userService.getCurrentUser();
        if(currentUser.getRole().equals(Role.system_admin)) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        if((currentUser.getRole().equals(Role.client) && (!request.getRemoteAddr().equals(currentUser.getIp())) && !request.getRemoteAddr().equals("0:0:0:0:0:0:0:1")) || currentUser.getRole().equals(Role.system_admin)) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        if(currentUser.getRole().equals(Role.client)) {
            userService.checkClientPermissions(currentUser, PermissionOperation.UPDATE);
        }

        File oldFile = new File(path);

        if (!oldFile.getPath().startsWith(currentUser.getRootDir())) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        if(!oldFile.exists() || oldFile.isDirectory()) {
            throw new BadRequestException("Select file");
        }

        File directory = new File(String.valueOf(oldFile.getParentFile()));

        if (!directory.getPath().startsWith(currentUser.getRootDir())) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        oldFile.delete();

        File targetFile = new File(directory.getPath() + File.separator + multipartFile.getOriginalFilename());
        try(OutputStream outputStream = new FileOutputStream(targetFile)) {
            IOUtils.copy(multipartFile.getInputStream(), outputStream);
            actionService.createAction(String.valueOf(PermissionOperation.UPDATE), currentUser.getUsername(), targetFile.getName());
        } catch (IOException e) {
            throw new BadRequestException();
        }
    }

    @PostMapping("/move")
    @ResponseStatus(HttpStatus.OK)
    public void moveFile(@RequestBody MoveFileRequest data, HttpServletRequest request) {
        User currentUser = userService.getCurrentUser();
        if(!currentUser.getRole().equals(Role.documents_admin)) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        File oldFile = new File(data.getPath());
        File newDirectory = new File(new File(currentUser.getRootDir()).getParentFile().getPath() + File.separator + data.getNewPath());

        System.out.println(newDirectory.getPath());
        if (!oldFile.getPath().startsWith(currentUser.getRootDir()) || !newDirectory.getPath().startsWith(currentUser.getRootDir())) {
            throw new ForbiddenException("You don't have permission to do this operation");
        }

        if(!oldFile.exists() || oldFile.isDirectory()) {
            throw new BadRequestException("Select file to move");
        }

        if( !newDirectory.exists()  || !newDirectory.isDirectory()) {
            throw new BadRequestException("Select directory to move into");
        }

        try {
            Files.move(Paths.get(oldFile.getPath()), Paths.get(newDirectory.getPath() + File.separator + oldFile.getName()));
            actionService.createAction("MOVE", currentUser.getUsername(), oldFile.getName());
        } catch (IOException e) {
            throw new BadRequestException();
        }
    }
}
