package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.response.UserResponse;
import com.uniquindio.skyline.application.exception.EmailAlreadyRegisteredException;
import com.uniquindio.skyline.application.usecase.RegisterCustomerUseCase;
import com.uniquindio.skyline.domain.entity.User;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import com.uniquindio.skyline.infrastructure.rest.mapper.UserMapper;
import com.uniquindio.skyline.infrastructure.security.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    private static final String REGISTER_URL = "/api/auth/register";

    private static final String REGISTER_JSON = """
            {
            "email": "laura@mail.com", "password": "Secret123",
            "idDocument": "1001", "documentType": "CITIZENSHIP_ID",
            "firstName": "Laura", "secondName": "Camila",
            "lastName1": "Duarte", "lastName2": "Quintero",
            "birthDate": "2000-05-10", "gender": "FEMALE"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterCustomerUseCase registerCustomerUseCase;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private UserMapper userMapper;

    @Test
    void shouldAnswer201WhenCustomerIsRegistered() throws Exception {
        // Arrange
        User user = mock(User.class);
        when(registerCustomerUseCase.execute("laura@mail.com", "Secret123", "1001",
                DocumentType.CITIZENSHIP_ID, "Laura", "Camila", "Duarte", "Quintero",
                LocalDate.of(2000, 5, 10), Gender.FEMALE)).thenReturn(user);
        when(userMapper.toRegisterResponse(user))
                .thenReturn(new UserResponse("user-1", "laura@mail.com", "CUSTOMER", null));

        // Act & Assert
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_JSON))
                .andExpect(status().isCreated())           // 201
                .andExpect(jsonPath("$.id").value("user-1"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.password").doesNotExist());
        verify(userMapper).toRegisterResponse(user);
    }

    @Test
    void shouldAnswer400WhenRequestIsInvalid() throws Exception {
        // Arrange
        String requestJson = """
                {
                "email": "not-an-email", "password": "Secret123",
                "idDocument": "1001", "documentType": "CITIZENSHIP_ID",
                "firstName": "Laura", "lastName1": "Duarte",
                "birthDate": "2000-05-10", "gender": "FEMALE"
                }
                """;

        // Act & Assert
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())        // 400
                .andExpect(jsonPath("$.errors.email").value("The email is not valid"));
        verifyNoInteractions(registerCustomerUseCase);
    }

    @Test
    void shouldAnswer409WhenCustomerEmailIsAlreadyRegistered() throws Exception {
        // Arrange
        when(registerCustomerUseCase.execute(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new EmailAlreadyRegisteredException());

        // Act & Assert
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_JSON))
                .andExpect(status().isConflict())          // 409
                .andExpect(jsonPath("$.message").value("The email is already registered"));
    }

    @Test
    void shouldAnswer422WhenCustomerBreaksABusinessRule() throws Exception {
        // Arrange
        when(registerCustomerUseCase.execute(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new DomainRuleException("The password must have a number"));

        // Act & Assert
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_JSON))
                .andExpect(status().isUnprocessableContent())  // 422
                .andExpect(jsonPath("$.message").value("The password must have a number"));
    }
}
