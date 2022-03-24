package app.mymemo.backend.appuser;

import app.mymemo.backend.exception.UnauthorizedRequestException;
import app.mymemo.backend.security.JWTTokenService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AppUserController {
	private final AppUserService userService;
    private final JWTTokenService jwtTokenService;

	@GetMapping
    public ResponseEntity<List<AppUser>> getUsers(){
        // TODO should only admin get this

        return ResponseEntity.ok().body(userService.findAllUsers());
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<UserDetails> getUser(HttpServletRequest request,
            @PathVariable String id){

        String verifiedJwtUserId =
                jwtTokenService.getAppUserIdFromHttpRequest(request);

        if (!id.equals(verifiedJwtUserId))
            throw new UnauthorizedRequestException();

        return ResponseEntity.ok().body(userService.loadUserById(id));
    }
	
	// To update user data
	@PostMapping("/{id}/update")
    public ResponseEntity<AppUser> updateUser(
            HttpServletRequest request,
            @PathVariable String id,
            @RequestBody AppUser appUser){

        String verifiedJwtUserId =
                jwtTokenService.getAppUserIdFromHttpRequest(request);

        if (!id.equals(verifiedJwtUserId))
            throw new UnauthorizedRequestException();

        return ResponseEntity.ok().body( userService.updateUser(id,appUser));
    }

//    @GetMapping("/{id}/roles")

	@PostMapping("/{id}/roles/add-role")
    public ResponseEntity<?> addRoleToAppUser(
            HttpServletRequest request,
            @PathVariable String id,
            @RequestBody RoleToUserForm form){
        // TODO update wrt front end, it can be multiple chooses add and delete together
        String verifiedJwtUserId =
                jwtTokenService.getAppUserIdFromHttpRequest(request);

        if (!id.equals(verifiedJwtUserId))
            throw new UnauthorizedRequestException();

        userService.addRoleToUser(id, form.getUsername(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

//	@PostMapping(“/{user-id}/reset-password”)
//	@PostMapping(“/{user-id}/new-password”)
}

@Data
class RoleToUserForm{
    private String username;
    private String roleName;
}

// TODO write 1 or 2 return userDetails class
/* current return
{
    "id": "6234ddcc3684ef58199f9416",
    "firstName": "erkam",
    "lastName": "guresen",
    "email": "erkamguresen@gmail.com",
    "password": "$2a$10$VmKikDiLkT1O6He7zFxUAesjDugwTB/OkMLy5depU9u4i06FqJoHq",
    "roles": [
        "APP_USER_ROLE"
    ],
    "enabled": true,
    "accountEnabled": true,
    "authorities": [
        {
            "authority": "APP_USER_ROLE"
        }
    ],
    "username": "erkamguresen@gmail.com",
    "credentialsNonExpired": true,
    "accountNonExpired": true,
    "accountLocked": false,
    "accountNonLocked": true
}
 */
