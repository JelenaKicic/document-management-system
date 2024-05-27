package sigurnost.dms.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sigurnost.dms.exceptions.BadRequestException;
import sigurnost.dms.exceptions.ForbiddenException;
import sigurnost.dms.models.dto.KeycloakToken;
import sigurnost.dms.models.dto.User;
import sigurnost.dms.models.dto.UserPermission;
import sigurnost.dms.models.entities.PermissionEntity;
import sigurnost.dms.models.enums.PermissionOperation;
import sigurnost.dms.models.enums.Role;
import sigurnost.dms.repositories.UserPermissionsRepository;
import org.modelmapper.ModelMapper;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserService {
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;
    private final Gson gson;
    private final UserPermissionsRepository userPermissionsRepository;

    public UserService(UserPermissionsRepository userPermissionsRepository, ModelMapper modelMapper) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.httpHeaders = new HttpHeaders();
        this.restTemplate = new RestTemplateBuilder().build();
        this.userPermissionsRepository = userPermissionsRepository;
        this.modelMapper = modelMapper;
    }

    @PersistenceContext
    private EntityManager entityManager;

    public String getUsersAdminToken(){
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String,String>();

        map.add("grant_type", "client_credentials");
        map.add("client_id", "admin-cli");
        map.add("client_secret","6988c11e-297f-4613-b5e1-5b8eebd45e05");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, httpHeaders);

        ResponseEntity<?> accessTokenResponse = restTemplate.postForEntity("http://localhost:8180/auth/realms/dms/protocol/openid-connect/token", request, String.class);

        KeycloakToken json = gson.fromJson(accessTokenResponse.getBody().toString(), KeycloakToken.class);

        String accessToken = json.getAccess_token();
        return accessToken;
    }

    public boolean checkIfUserIsClient(String userId) {
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + this.getUsersAdminToken());
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(httpHeaders);
        String response = restTemplate.exchange("http://localhost:8180/auth/admin/realms/dms/users/" + userId + "/role-mappings/realm", HttpMethod.GET, httpEntity, String.class).getBody();


        List<Map<String, String>> rolesList = gson.fromJson(response, ArrayList.class);
        boolean isClient = false;
        for(Map<String, String> role : rolesList) {
            if(role.get("name").equals("client")) {
                isClient = true;
            }
        }

        return isClient;
    }

    public User getCurrentUser() {
        User currentUser = new User();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        currentUser.setId(jwt.getClaims().get("sub").toString());
        currentUser.setUsername(jwt.getClaims().get("preferred_username").toString());
        currentUser.setEmail(jwt.getClaims().get("email").toString());

        Map<String, List<String>> rolesMap = gson.fromJson(jwt.getClaims().get("realm_access").toString(), HashMap.class);
        if(rolesMap == null) {
            throw new BadRequestException();
        }
        List<String> userRealmRoles = rolesMap.get("roles");

        if(userRealmRoles == null) {
            throw new BadRequestException();
        }

        if(userRealmRoles.contains("client")) {
            currentUser.setRole(Role.client);
        } else if(userRealmRoles.contains("documents_admin")) {
            currentUser.setRole(Role.documents_admin);
        } else if(userRealmRoles.contains("system_admin")) {
            currentUser.setRole(Role.system_admin);
        } else {
            throw new BadRequestException();
        }

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + this.getUsersAdminToken());
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(httpHeaders);
        String response = restTemplate.exchange("http://localhost:8180/auth/admin/realms/dms/users/" + currentUser.getId(), HttpMethod.GET, httpEntity, String.class).getBody();
        Map<String, Map<String,  List<String>>> userMap = gson.fromJson(response, HashMap.class);

        currentUser.setIp(userMap.get("attributes").get("ip").get(0));
        currentUser.setRootDir(userMap.get("attributes").get("rootdir").get(0));

        return  currentUser;
    }

    public void addUserPermission(String userId, PermissionOperation permissionOperation) throws BadRequestException {
        List<PermissionEntity> existing = userPermissionsRepository.findByUserIdAndOperation(userId, permissionOperation.toString());
        if(existing.size() != 0) {
            throw new BadRequestException("User already have permission");
        }

        UserPermission userPermission = new UserPermission();
        userPermission.setUserId(userId);
        userPermission.setOperation(permissionOperation);

        PermissionEntity permissionEntity = modelMapper.map(userPermission, PermissionEntity.class);
        permissionEntity.setId(null);
        permissionEntity = userPermissionsRepository.saveAndFlush(permissionEntity);
    }

    public void deleteUserPermission(Integer id) {
        Optional<PermissionEntity> existing = userPermissionsRepository.findById(id);
        if(!existing.isPresent()) {
            throw new BadRequestException("User don't have permission");
        }

        userPermissionsRepository.deleteById(id);
    }

    public List<UserPermission> getUserPermissions(String id) {
        return userPermissionsRepository.findByUserId(id).stream().map(e -> modelMapper.map(e, UserPermission.class)).collect(Collectors.toList());
    }

    public void checkClientPermissions(User currentUser, PermissionOperation permissionOperation) {
        boolean hasPermission = false;
        for (UserPermission permission : getUserPermissions(currentUser.getId())) {
            if (permission.getOperation().equals(permissionOperation)) {
                hasPermission = true;
            }
        }

        if(!hasPermission) throw new ForbiddenException("You don't have permission to do this operation");
    }
}
