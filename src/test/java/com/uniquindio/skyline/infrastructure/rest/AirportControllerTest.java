package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.response.AirportResponse;
import com.uniquindio.skyline.application.usecase.ListAirportsUseCase;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.infrastructure.rest.mapper.AirportMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AirportController.class)
public class AirportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListAirportsUseCase listAirportsUseCase;

    @MockitoBean
    private AirportMapper mapper;

    @Test
    void shouldAnswer200WithAirports() throws Exception {
        // Arrange
        when(listAirportsUseCase.execute()).thenReturn(List.of(Airport.EL_DORADO));
        when(mapper.toAirportResponse(Airport.EL_DORADO))
                .thenReturn(new AirportResponse("BOG", "El Dorado International Airport", "Bogotá", "Colombia"));

        // Act & Assert
        mockMvc.perform(get("/api/airports"))
                .andExpect(status().isOk())                // 200
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].code").value("BOG"))
                .andExpect(jsonPath("$[0].city").value("Bogotá"));
    }
}
