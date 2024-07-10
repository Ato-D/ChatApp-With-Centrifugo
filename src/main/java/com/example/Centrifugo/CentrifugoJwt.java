package com.example.Centrifugo;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CentrifugoJwt {

    private  final CentrifugoConfiguration centrifugo;

    public String createJWT(OidcUser user) {

        final String CENTRIFUGO_SECRET = centrifugo.getSecret();

        //The JWT signature algorithm to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    }
}
