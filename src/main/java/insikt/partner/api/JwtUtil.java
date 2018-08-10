package insikt.partner.api;

import com.google.common.base.Preconditions;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.apache.shiro.codec.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class JwtUtil {

    public static String generateBearer(
            final String privateKey,
            final List<String> audience,
            final String issuer,
            final String subject,
            final Long expirationPeriod,
            final Map<String, Object> customClaims) {
        return generateBearer(privateKey, audience, issuer, subject, expirationPeriod, null, customClaims);
    }

    private static String generateBearer(
            final String privateKey,
            final List<String> audience,
            final String issuer,
            final String subject,
            final Long expirationPeriod,
            final Date expirationTime,
            final Map<String, Object> claims) {

        Preconditions.checkNotNull(privateKey);
        Preconditions.checkNotNull(audience);
        Preconditions.checkNotNull(issuer);
        Preconditions.checkNotNull(subject);
        Preconditions.checkArgument(expirationPeriod != null || expirationTime != null);

        String bearer;
        try {
            // PKCS8 decode the encoded RSA private key
            final byte[] decodeBase64 = Base64.decode(privateKey);
            final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodeBase64);
            final KeyFactory kf = KeyFactory.getInstance("RSA");
            final RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
            final JWSSigner signer = new RSASSASigner(rsaPrivateKey);
            final Instant currentDateTime = Instant.now();

            final JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder();

            // all existing claims (registered + custom)
            for (String key : claims.keySet()) {
                claimsSetBuilder.claim(key, claims.get(key));
            }

            // overwrite registered claims with new values
            claimsSetBuilder
                    .audience(audience)
                    .issuer(issuer)
                    .subject(subject)
                    .jwtID(UUID.randomUUID().toString())
                    .expirationTime(expirationPeriod != null
                            ? Date.from(currentDateTime.plusSeconds(TimeUnit.MILLISECONDS.toSeconds(expirationPeriod)))
                            : expirationTime)
                    .issueTime(Date.from(currentDateTime));

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSetBuilder.build());

            signedJWT.sign(signer);

            bearer = signedJWT.serialize();

        } catch (JOSEException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Unable to generate JWT", e);
        }
        return bearer;
    }
}
