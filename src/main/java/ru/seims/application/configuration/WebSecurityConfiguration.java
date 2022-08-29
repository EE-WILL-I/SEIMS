package ru.seims.application.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import ru.seims.application.security.*;
import ru.seims.application.security.handler.CustomAccessDeniedHandler;
import ru.seims.application.security.handler.JwtAuthenticationSuccessHandler;
import ru.seims.application.security.service.AuthorizationService;
import ru.seims.database.UserService;
import ru.seims.database.proccessing.SQLExecutor;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final String rememberMeSecret = PropertyReader.getPropertyValue(PropertyType.SERVER, "app.rememberMeSecret");
    private final byte orgEditorAuths   = 1;
    private final byte regEditorAuths   = 2;
    private final byte stateEditorAuths = 3;
    private final byte dbEditorAuths    = 4;
    private final byte appEditorAuths   = 5;
    private final String[] auths = new String[] {"org_editor", "region_editor", "state_editor", "db_editor", "app_editor"};

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //String usersQuery = PropertyReader.getPropertyValue(PropertyType.DATABASE, "datasource.usersByUsernameQuery");
        //String authsQuery = PropertyReader.getPropertyValue(PropertyType.DATABASE, "datasource.authoritiesByUsernameQuery");
        auth.userDetailsService(userService()).passwordEncoder(MD5PasswordEncoder.getInstance());
        /*auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(MD5PasswordEncoder.getInstance())
                .usersByUsernameQuery(SQLExecutor.getInstance().loadSQLResource(usersQuery))
                .authoritiesByUsernameQuery(SQLExecutor.getInstance().loadSQLResource(authsQuery));*/
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //http.addFilterBefore(requestFilter(), UsernamePasswordAuthenticationFilter.class);
        //http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/login", "/registration").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/test").permitAll()
                .antMatchers("/api/user/**").permitAll()
                .antMatchers("/org/**").permitAll()
                .antMatchers("/api/org/edit/**").hasAnyAuthority(getAuths(orgEditorAuths))
                .antMatchers("/api/region/**").hasAnyAuthority(getAuths(regEditorAuths))
                .antMatchers("/api/state/**").hasAnyAuthority(getAuths(stateEditorAuths))
                .antMatchers("/api/db/**").hasAnyAuthority(getAuths(dbEditorAuths))
                .antMatchers("/api/app/**").hasAnyAuthority(getAuths(appEditorAuths))
                .anyRequest().authenticated()
                .and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
                .accessDeniedPage("/login?error=noaccess")
                .and().formLogin()
                //.successHandler(authenticationSuccessHandler(userService()))
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error=failed")
                .permitAll();
        http.logout().logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .deleteCookies("remember-me")
                .and().rememberMe().key(rememberMeSecret);
                //.deleteCookies(AuthorizationService.getInstance().JWT_HOLDER_COOKIE_NAME)
                //.permitAll();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/resources/**", "/static/**");
    }

    @Bean
    public JwtAuthenticationEntryPoint authenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public JwtAuthenticationSuccessHandler authenticationSuccessHandler(UserService userService) {
        return new JwtAuthenticationSuccessHandler(userService);
    }

    @Bean
    public CustomAccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public UserService userService() {
        return new UserService();
    }

    private String[] getAuths(byte role) {
        String[] auths = new String[this.auths.length - role + 1];
        byte j = 0;
        for(int i = role - 1; i < this.auths.length; i++) {
            auths[j] = this.auths[i];
            j++;
        }
        return auths;
    }
}
