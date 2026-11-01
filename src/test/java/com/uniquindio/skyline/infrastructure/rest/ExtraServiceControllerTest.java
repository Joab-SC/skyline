package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.response.ExtraServiceResponse;
import com.uniquindio.skyline.application.usecase.CreateExtraServiceUseCase;
import com.uniquindio.skyline.application.usecase.SearchExtraServicesUseCase;
import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.PageResult;
import com.uniquindio.skyline.infrastructure.rest.mapper.ExtraServiceMapper;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExtraServiceController.class)
public class ExtraServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateExtraServiceUseCase createExtraServiceUseCase;

    @MockitoBean
    private SearchExtraServicesUseCase searchExtraServicesUseCase;

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
    void shouldAnswer422WhenBusinessRuleIsBroken() throws Exception {
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
                .andExpect(status().isUnprocessableContent());  //422

    }

    // ---------- GET /api/extra_services ----------

    @Test
    void shouldAnswerPageOfExtraServices() throws Exception {
        // Arrange
        ExtraService wifi = ExtraService.createExtraService("Wifi", null, 20, 40);
        when(searchExtraServicesUseCase.execute("wi", "airline-1", 0, 2))
                .thenReturn(new PageResult<>(List.of(wifi), 3, 2, 0, 2));
        when(mapper.toExtraServiceResponse(wifi))
                .thenReturn(new ExtraServiceResponse(wifi.getId(), "Wifi", null, 20, 40));

        // Act & Assert
        mockMvc.perform(get("/api/extra_services")
                        .param("search", "wi")
                        .param("airlineId", "airline-1")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())                // 200
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].nameService").value("Wifi"))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2));
    }

    @Test
    void shouldUseFirstPageOfTenWhenNoParamsAreSent() throws Exception {
        // Arrange
        when(searchExtraServicesUseCase.execute(null, null, 0, 10))
                .thenReturn(new PageResult<>(List.of(), 0, 0, 0, 10));

        // Act & Assert
        mockMvc.perform(get("/api/extra_services"))
                .andExpect(status().isOk())                // 200
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(searchExtraServicesUseCase).execute(null, null, 0, 10);
    }

    @Test
    void shouldAnswer400WhenPageSizeIsOutOfRange() throws Exception {
        // Arrange
        when(searchExtraServicesUseCase.execute(null, null, 0, 100))
                .thenThrow(new IllegalArgumentException("The page size must be between 1 and 50"));

        // Act & Assert
        mockMvc.perform(get("/api/extra_services").param("size", "100"))
                .andExpect(status().isBadRequest())        // 400
                .andExpect(jsonPath("$.message").value("The page size must be between 1 and 50"));
    }

    @Test
    void shouldAnswer400WhenPageIsNotANumber() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/extra_services").param("page", "abc"))
                .andExpect(status().isBadRequest())        // 400
                .andExpect(jsonPath("$.message").value("The parameter 'page' has the wrong format"));
    }

}
