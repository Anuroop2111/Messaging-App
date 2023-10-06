package com.Messenger.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JwtTokenValidateResponse {
    private boolean setFlag;
    private boolean invalidFlag;
    private String receivedJwtToken;
    private String username;
}
