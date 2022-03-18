package app.mymemo.backend.security.login;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Login route is defined in security. This controller is focused on
 * other features like refreshing access token or password recovery.
 */
@RestController
@RequestMapping(path = "api/v1/login")
@AllArgsConstructor
public class LoginController {
    //TODO tests

    private final LoginService loginService;

    //TODO void? it returns tokens
    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
       loginService.refreshAccessTokenByRefreshToken(request, response);
    }

    //TODO forget password request
//    @PostMapping("/forgotten-password")
//    public ResponseEntity<?> sendEmailForTheForgottenPassword(){}

    //TODO update password request
//    @PostMapping("/forgotten-password/reset")
//    public ResponseEntity<?> resetTheForgottenPassword(){}

}
