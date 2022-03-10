package app.mymemo.backend.appuser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides core user information.
 *
 * Author: Erkam Guresen
  */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Document
public class AppUser implements UserDetails {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    // In this application email is used as the username
    private String email;
    private String password;
    private List<AppUserRole> roles = new ArrayList<>();
    private boolean isAccountLocked = false;
    private boolean isAccountEnabled = false;

    /**
     * Basic AppUser constructor.
     *
     * @param firstName first name of the app user
     * @param lastName last name of the app user
     * @param email valid email address of the app user
     * @param password valid password of the app user
     * @param roles roles of the user
     */
    public AppUser(String firstName,
                   String lastName,
                   String email,
                   String password,
                   List<AppUserRole> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        // TODO decide what the front end will send in the first registration of
        this.roles = roles;
        this.isAccountLocked = false;
        this.isAccountEnabled = false;
    }

    /**
     * Returns the authorities granted to the user. Cannot return null.
     * @return  the authorities, sorted by natural key (never null)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<SimpleGrantedAuthority> authorities =
                new ArrayList<>();

        for (int i = 0; i < roles.size(); i++) {
            AppUserRole role = roles.get(i);
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(role.name());
            authorities.add(authority);
        }

        return authorities;
    }

    /**
     * Returns the (encrypted) password used to authenticate the user.
     * @return the (encrypted) password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username used to authenticate the user. Cannot return null.
     * @return the username (never null)
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indicates whether the user's account has expired. An expired account cannot be authenticated.
     * @return true if the user's account is valid (ie non-expired), false if no longer valid (ie expired)
     */
    @Override
    public boolean isAccountNonExpired() {
        //TODO this feature is not added
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be authenticated.
     * @return true if the user is not locked, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return !isAccountLocked;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired credentials prevent authentication.
     * @return true if the user's credentials are valid (ie non-expired), false if no longer valid (ie expired)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        //TODO this feature is not added
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be authenticated.
     * @return true if the user is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return isAccountEnabled;
    }
}
