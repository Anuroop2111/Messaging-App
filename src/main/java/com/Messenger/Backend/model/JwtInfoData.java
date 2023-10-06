package com.Messenger.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtInfoData {
    private String username;
    private String refSeries;
    private boolean isJwtExpiredFlag;
}
