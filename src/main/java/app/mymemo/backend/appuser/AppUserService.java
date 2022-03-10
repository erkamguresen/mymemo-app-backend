package app.mymemo.backend.appuser;

import app.mymemo.backend.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Provides core user service.
 *
 * Author: Erkam Guresen
 */
@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private static String USER_NOT_FOUND_MESSAGE =
            "User with email %s is not found";

    @NonNull
    private final AppUserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
//    private final ConfirmationTokenService confirmationTokenService;

    /**
     * Locates the user based on the email. In this implementation,
     * all email strings must be lowercase.
     * @param email the EMAIL identifying the user whose data is required.
     * @return a fully populated user record (never null)
     * @throws UsernameNotFoundException  if the user could not be found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
           return userRepository.findUserByEmail(email);
        } catch (Exception e){
            throw new UsernameNotFoundException(
                    String.format(USER_NOT_FOUND_MESSAGE,email) );
        }
    }
    
    /**
     * Locates the user based on the id. 
     * @param id the id of the user whose data is required.
     * @return a fully populated user record (never null)
     * @throws UsernameNotFoundException  if the user could not be found
     */
    public UserDetails loadUserById(String id) throws UsernameNotFoundException {
        try {
           return userRepository.findUserById(id);
        } catch (Exception e){
            throw new UsernameNotFoundException(
                    String.format(USER_NOT_FOUND_MESSAGE,id) );
        }
    }

    /**
     * Enables the account of the AppUser.
     * @param appUser the app user record
     * @return the saved user record (never null)
     */
    public AppUser enableAppUser(AppUser appUser){
        //TODO do not return password ? check controller

        appUser.setAccountEnabled(true);

        return userRepository.save(appUser);
    }

    /**
     * Returns the list of the current app users.
     * @return a list containing all app users or an empty list if there is no registered user
     */
    public List<AppUser> findAllUsers(){

        List<AppUser> result = new ArrayList<>();

        userRepository.findAll().forEach(result::add);

        return result;
    }

    /**
     * Adds a new role to the app user.
     * @param email the email of the app user
     * @param roleName name of the new role
     * @throws UsernameNotFoundException  if the user could not be found
     */
    public void addRoleToUser(String email, String roleName) throws UsernameNotFoundException {

        AppUser user = userRepository.findUserByEmail(email);

        AppUserRole role = Enum.valueOf(AppUserRole.class, roleName);
        
        // TODO check this        
        boolean contains = false;
//        for (AppUserRole userRole : AppUserRole.values()) {
            if (user.getRoles().contains(role)) {
                contains = true;
//                break;
            }
//        }
        
        if (!contains) {
        	user.getRoles().add(role);
        
	        // TODO Check if we need this step
	        userRepository.save(user);
		}

    }

    /**
     * Saves the new details of the existing user.
     * @param user
     * @return
     */
    public AppUser updateUser(String id, AppUser user){
        AppUser existingUser = userRepository.findUserById(id);
        existingUser.updateAllowedPartsFromUserObject(user);

        //TODO check id in the uri and the token id
        // TODO do not return the unnecessary details (password etc.)
        return userRepository.save(existingUser);
    }

    /**
     * Checks existing users and if the user does not exist creates a new user entity
     * @param appUser signup details of new user creation request
     * @return a confirmation token of registration
     */
    public String signUpUser(AppUser appUser){
        boolean userExists = userRepository.findUserByEmail(appUser.getEmail()) != null
                ?true
                :false ;

        if (userExists){
            throw new BadRequestException("Email is already registered.");
        }

        // here is de encoded password
        String encodedPassword =
                bCryptPasswordEncoder.encode(appUser.getPassword());

        appUser.setPassword(encodedPassword);
        userRepository.save(appUser);

        String token = UUID.randomUUID().toString();
//        ConfirmationToken confirmationToken = new ConfirmationToken(
//                token,
//                LocalDateTime.now(),
//                LocalDateTime.now().plusMinutes(15),
//                appUser
//        );
//
//        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return token;

    }
}
