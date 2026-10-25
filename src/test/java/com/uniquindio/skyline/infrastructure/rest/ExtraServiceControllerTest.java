package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.usecase.CreateExtraServiceUseCase;
import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.infrastructure.rest.mapper.ExtraServiceMapper;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExtraServiceController.class)
public class ExtraServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateExtraServiceUseCase createExtraServiceUseCase;

    @MockitoBean
    private ExtraServiceMapper mapper;

    @Test
    void shouldAnswerCreateExtraServiceWithValidData() throws Exception {
        Airline airline = Airline.createAirline("Mega airline", 10, 100);
        ExtraService extraService = airline.createExtraService("Hamburguer", "", 11, 22);
        when(createExtraServiceUseCase.execute(any(), any(), any(), anyDouble(), anyDouble())).
                thenReturn(extraService);

        String requestJson = """
                {
                "airlineId" : "air-1", "nameService": "Hamburguer",
                "description": "", "nationalPrice": 11, "internationalPrice":22
                }
               """;
        mockMvc.perform(post("/api/extra_services") // simular POST
                        .contentType(MediaType.APPLICATION_JSON) //esto que envio aqui es Json ->415
                        .content(requestJson)) //adjunta el cuerpo Json
                .andExpect(status().isCreated())   // HTTP -> 201 (CREATED)
                .andExpect(header().exists("Location"));

    }

    @Test
    void shouldAnswer404WhenAirlineDoesNotExist() throws Exception {
        when(createExtraServiceUseCase.execute(any(), any(), any(), anyDouble(), anyDouble())).
                thenThrow(new NoSuchElementException());

        String requestJson = """
                {
                "airlineId" : "air-1", "nameService": "Hamburguer",
                "description": "", "nationalPrice": 11, "internationalPrice":22
                }
               """;
        mockMvc.perform(post("/api/extra_services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound());

    }

    @Test
    void shouldAnswer400WhenBusinessRuleIsBroken() throws Exception {
        when(createExtraServiceUseCase.execute(any(), any(), any(), anyDouble(), anyDouble())).
                thenThrow(new DomainRuleException("The price must be greater than 0."));

        String requestJson = """
                {
                "airlineId" : "air-1", "nameService": "Hamburguer",
                "description": "", "nationalPrice": 11, "internationalPrice":-1
                }
               """;
        mockMvc.perform(post("/api/extra_services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

    }

}
