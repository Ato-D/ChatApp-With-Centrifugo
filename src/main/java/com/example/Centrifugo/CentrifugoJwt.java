package com.example.Centrifugo;


import com.example.Centrifugo.config.CentrifugoConfiguration;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class CentrifugoJwt {

    private  final CentrifugoConfiguration centrifugo;

    public String createJWT(OidcUser user) {

        final String CENTRIFUGO_SECRET = centrifugo.getSecret();

        //The JWT signature algorithm to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //Get Current Day/Time to set as IssuedAt
        long nowMills = System.currentTimeMillis();
        Date currentDate = new Date(nowMills);

        //Get 1 month from the current Day/Time to set as Expiration
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, + 1);
        Date futureDate = cal.getTime();

        //Sign token Using Custom Secret
        Key signifyKey = new SecretKeySpec(CENTRIFUGO_SECRET.getBytes(),
                          signatureAlgorithm.getJcaName());

        //Setting the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setIssuer("CentrifugoWeb")
                .setAudience("Centrifugo")
                .setIssuedAt(currentDate)
                .setExpiration(futureDate)
                .setSubject(user.getAttributes().get("sub").toString())
                .signWith(signifyKey,signatureAlgorithm);

        return builder.compact();

    }
}
