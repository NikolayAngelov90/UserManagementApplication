package com.usermanagement.web.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JwtAuthenticationResponse {

    String token;
}
