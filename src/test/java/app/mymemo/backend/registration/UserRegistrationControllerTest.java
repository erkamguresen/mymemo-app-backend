package app.mymemo.backend.registration;

import app.mymemo.backend.appuser.AppUserRepository;
import app.mymemo.backend.registration.token.ConfirmationTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

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
    @Autowired
    private ConfirmationTokenRepository tokenRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        tokenRepository.deleteAll();
    }

    @Test
    void itCanRegisterANewAppUser() throws Exception {
        //given
        String url = "http://localhost:" + port + "/api/v1/registration";

       ResultActions resultActions = mockMvc.perform(
                post(url).contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"firstName\":\"jhon\",\n" +
                                "    \"lastName\":\"doe\",\n" +
                                "    \"email\":\"admin@mymemo.app\",\n" +
                                "    \"password\":\"password\"\n" +
                                "}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    @Test
    void itCanRegisterAnUnconfirmedAppUser() throws Exception {
        //given
        String url = "http://localhost:" + port + "/api/v1/registration";

        ResultActions resultActions = mockMvc.perform(
                        post(url).contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"firstName\":\"jhon\",\n" +
                                        "    \"lastName\":\"doe\",\n" +
                                        "    \"email\":\"admin@mymemo.app\",\n" +
                                        "    \"password\":\"password\"\n" +
                                        "}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));

        //then
        mockMvc.perform(
                        post(url).contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"firstName\":\"jhon\",\n" +
                                        "    \"lastName\":\"doe\",\n" +
                                        "    \"email\":\"admin@mymemo.app\",\n" +
                                        "    \"password\":\"password2\"\n" +
                                        "}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    @Test
    void itCanConfirmARegistrationToken() throws Exception {
        //given
        String url = "http://localhost:" + port + "/api/v1/registration";

        // register
        ResultActions resultActions = mockMvc.perform(
                        post(url).contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"firstName\":\"jhon\",\n" +
                                        "    \"lastName\":\"doe\",\n" +
                                        "    \"email\":\"admin@mymemo.app\",\n" +
                                        "    \"password\":\"password\"\n" +
                                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));

        // confirm the token
        MvcResult mvcResult = resultActions.andReturn();
        String confirmationToken = mvcResult.getResponse().getContentAsString();
        mockMvc.perform(get(url+"/confirm?token="+confirmationToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("confirmed"));
    }

    @Test
    void itShouldReturn400WhenTryToConfirmAnInvalidRegistrationToken() throws Exception {
        //given
        String url = "http://localhost:" + port + "/api/v1/registration";

        // confirm an invalid token
        mockMvc.perform(get(url+"/confirm?token="+"ThisRandomTokenShouldFail"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    void itShouldReturn400WhenTryToConfirmAConfirmedRegistrationToken() throws Exception {
        //given
        String url = "http://localhost:" + port + "/api/v1/registration";

        // register
        ResultActions resultActions = mockMvc.perform(
                        post(url).contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"firstName\":\"jhon\",\n" +
                                        "    \"lastName\":\"doe\",\n" +
                                        "    \"email\":\"admin@mymemo.app\",\n" +
                                        "    \"password\":\"password\"\n" +
                                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));

        // confirm the token
        MvcResult mvcResult = resultActions.andReturn();
        String confirmationToken = mvcResult.getResponse().getContentAsString();
        mockMvc.perform(get(url+"/confirm?token="+confirmationToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("confirmed"));

        // then the token again
        mvcResult = resultActions.andReturn();
        confirmationToken = mvcResult.getResponse().getContentAsString();
        mockMvc.perform(get(url+"/confirm?token="+confirmationToken))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void itShouldReturn404WhenTryToRegisterAnExistingAndConfirmedUser() throws Exception {
        //given
        String url = "http://localhost:" + port + "/api/v1/registration";

        // register
        ResultActions resultActions = mockMvc.perform(
                        post(url).contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"firstName\":\"jhon\",\n" +
                                        "    \"lastName\":\"doe\",\n" +
                                        "    \"email\":\"admin@mymemo.app\",\n" +
                                        "    \"password\":\"password\"\n" +
                                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));

        // confirm the token
        MvcResult mvcResult = resultActions.andReturn();
        String confirmationToken = mvcResult.getResponse().getContentAsString();
        mockMvc.perform(get(url+"/confirm?token="+confirmationToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("confirmed"));

        // then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                        "    \"firstName\":\"jhon\",\n" +
                                        "    \"lastName\":\"doe\",\n" +
                                        "    \"email\":\"admin@mymemo.app\",\n" +
                                        "    \"password\":\"password\"\n" +
                                        "}")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}