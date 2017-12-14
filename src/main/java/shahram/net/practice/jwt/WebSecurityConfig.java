package shahram.net.practice.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/", "/home").permitAll()
            .antMatchers("/greetings").hasRole("admin")
            .anyRequest().hasRole("user")
            .and()
            .formLogin()
            .loginPage("/login")
            .passwordParameter("jwt-token")
            .permitAll()
            .and()
            .logout()
            .permitAll();
        // .and()
        // .sessionManagement().maximumSessions(1)
        // .maxSessionsPreventsLogin(true);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {
        builder.authenticationProvider(new JwtAuthenticationProvider());
    }
}
