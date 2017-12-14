package shahram.net.practice.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JwtAuthenticationProvider implements AuthenticationProvider {
    Logger log = LoggerFactory.getLogger(getClass());

    public static final String SECRET = "secret";
    public static final long TIMEOUT = 1000 * 60 * 60 * 24 * 365;

    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        if (auth.getCredentials() != null) {
            try {
                Date now = new Date();
                log.info("Token: \n{}", Jwts.builder().setSubject(auth.getName()).claim("roles", "user,admin").setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + TIMEOUT))
                    .signWith(SignatureAlgorithm.HS256, SECRET).compact());
                Jws<Claims> claim = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(auth.getCredentials().toString().trim());
                String user = claim.getBody().getSubject();
                String[] roles = claim.getBody().get("roles", String.class).split(",");
                if (user != null && user.equals(auth.getName())) {
                    return new UsernamePasswordAuthenticationToken(auth.getPrincipal(), null, authorities(roles));
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
            return null;
        }
        return auth;
    }

    public boolean supports(Class<?> clazz) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(clazz);
    }

    private List<GrantedAuthority> authorities(String[] roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return authorities;
    }
}
