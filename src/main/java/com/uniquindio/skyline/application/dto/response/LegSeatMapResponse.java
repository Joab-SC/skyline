package com.uniquindio.skyline.application.dto.response;

import java.util.List;

public record LegSeatMapResponse(
        String legId,
        List<LegSeatResponse> seats
) {}