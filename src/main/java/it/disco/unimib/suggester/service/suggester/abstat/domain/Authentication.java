package it.disco.unimib.suggester.service.suggester.abstat.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Authentication {
    private String access_token;
    private String token_type;
    private long expires_in;
    private String scope;
    private String jti;
}