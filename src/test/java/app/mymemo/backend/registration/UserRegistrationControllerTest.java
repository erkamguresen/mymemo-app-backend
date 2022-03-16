package app.mymemo.backend.registration;

import app.mymemo.backend.appuser.AppUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserRegistrationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private  RegistrationService registrationService;
    @Autowired
    private AppUserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void itCanRegisterANewAppUser() throws Exception {
        //given
        String url = "http://localhost:" + port + "/api/v1/registration";

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .build();

       ResultActions resultActions = mockMvc.perform(
                post(url).contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"firstName\":\"jhon\",\n" +
                                "    \"lastName\":\"doe\",\n" +
                                "    \"email\":\"jhondoe@gmail.com\",\n" +
                                "    \"password\":\"password\"\n" +
                                "}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    @Test
    @Disabled
    void itShouldReturn404WhenTryToRegisterAnExistingUser() throws Exception {
        //TODO add condition to activated
        //given
        String url = "http://localhost:" + port + "/api/v1/registration";

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(url)
                .build();

        ResultActions resultActions = mockMvc.perform(
                        post(url).contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"firstName\":\"jhon\",\n" +
                                        "    \"lastName\":\"doe\",\n" +
                                        "    \"email\":\"jhondoe@gmail.com\",\n" +
                                        "    \"password\":\"password\"\n" +
                                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User registered. Token: ")));

        resultActions = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                        "    \"firstName\":\"jhon\",\n" +
                                        "    \"lastName\":\"doe\",\n" +
                                        "    \"email\":\"jhondoe@gmail.com\",\n" +
                                        "    \"password\":\"password\"\n" +
                                        "}")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}