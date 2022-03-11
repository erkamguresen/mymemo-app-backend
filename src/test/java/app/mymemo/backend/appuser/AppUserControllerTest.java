package app.mymemo.backend.appuser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.*;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AppUserControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        String email = "jhondoe@gmail.com";
        List<AppUserRole> roles = new ArrayList<>();
        roles.add(AppUserRole.APP_USER_ROLE);
        AppUser user = new AppUser(
                "John",
                "Doe",
                email,
                "password",
                roles
        );
        userRepository.save(user);

        email = "janedoe@gmail.com";
        roles = new ArrayList<>();
        roles.add(AppUserRole.APP_ADMIN_ROLE);

        user = new AppUser(
                "Jane",
                "Doe",
                email,
                "password",
                roles
        );
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void getUsers() throws JsonProcessingException {
        String url = "http://localhost:" + port + "/api/v1/users";

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .build();
        HttpEntity<String> requestEntity = new HttpEntity<>(null, null);
        ResponseEntity<String> response = testRestTemplate.exchange(builder.toString(), HttpMethod.GET, requestEntity,
                String.class);

        System.out.println(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());

        String[] userStrings = response.getBody()
                .replace("},{", "} split here {")
                .split(" split here ");

        System.out.println(userStrings[0]);
        assertThat(userStrings[0])
                .contains("\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"jhondoe@gmail.com\",\"password\":\"password\",\"roles\":[\"APP_USER_ROLE\"]")
                .contains("\"username\":\"jhondoe@gmail.com\"")
                .contains("\"authorities\":[{\"authority\":\"APP_USER_ROLE\"}]")
                .contains("\"enabled\":false")
                .contains("\"credentialsNonExpired\":true")
                .contains("\"accountNonExpired\":true")
                .contains("\"accountEnabled\":false")
                .contains("\"accountLocked\":false")
                .contains("\"accountNonLocked\":true");

        assertThat(userStrings[1])
                .contains("\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"email\":\"janedoe@gmail.com\",\"password\":\"password\",\"roles\":[\"APP_ADMIN_ROLE\"]")
                .contains("\"username\":\"janedoe@gmail.com\"")
                .contains("\"authorities\":[{\"authority\":\"APP_ADMIN_ROLE\"}]")
                .contains("\"enabled\":false")
                .contains("\"credentialsNonExpired\":true")
                .contains("\"accountNonExpired\":true")
                .contains("\"accountEnabled\":false")
                .contains("\"accountLocked\":false")
                .contains("\"accountNonLocked\":true");

    }

    @Test
    void getUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void addRoleToAppUser() {
    }
}