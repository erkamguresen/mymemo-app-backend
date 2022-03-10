package app.mymemo.backend.appuser;

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

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AppUserController {
	private final AppUserService userService;

	@GetMapping
    public ResponseEntity<List<AppUser>> getUsers(){

        return ResponseEntity.ok().body(userService.findAllUsers());
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<UserDetails> getUser(@PathVariable String id){

        return ResponseEntity.ok().body(userService.loadUserById(id));
    }
	
	// To update user data
	@PostMapping("/{id}/update")
    public ResponseEntity<AppUser> updateUser(
            @PathVariable String id,
            @RequestBody AppUser appUser){
               return ResponseEntity.ok().body( userService.updateUser(id,appUser));
    }

//    @GetMapping("/{id}/roles")

    // TODO Bug not working and I do not know why?
	@PostMapping("/{id}/roles/add-role")
    public ResponseEntity<?> addRoleToAppUser(
            @PathVariable String id,
            @RequestBody RoleToUserForm form){

        // TODO update wrt front end, it can be multiple chooses add and delete together

        userService.addRoleToUser(form.getUsername(), form.getRoleName());
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
