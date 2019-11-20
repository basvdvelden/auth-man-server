package nl.management.auth.server.config;

import nl.management.auth.server.security.filters.BlacklistFilter;
import nl.management.auth.server.security.filters.JWTFilter;
import nl.management.auth.server.security.filters.PinVerificationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

    @Autowired
    private BlacklistFilter blacklistFilter;

    @Autowired
    private JWTFilter jwtFilter;

    @Autowired
    private PinVerificationFilter pinVerificationFilter;

    @Bean
    public FilterRegistrationBean<PinVerificationFilter> filterRegistrationBean() {
        FilterRegistrationBean<PinVerificationFilter> regBean = new FilterRegistrationBean<>();
        regBean.setFilter(pinVerificationFilter);
        regBean.setOrder(3);

        return regBean;
    }

    @Bean
    public FilterRegistrationBean<JWTFilter> jwtFilterRegistrationBean() {
        FilterRegistrationBean<JWTFilter> regBean = new FilterRegistrationBean<>();
        regBean.setFilter(jwtFilter);
        regBean.setOrder(2);

        return regBean;
    }

    @Bean
    public FilterRegistrationBean<BlacklistFilter> blacklistFilterRegistrationBean() {
        FilterRegistrationBean<BlacklistFilter> regBean = new FilterRegistrationBean<>();
        regBean.setFilter(blacklistFilter);
        regBean.setOrder(1);

        return regBean;
    }
}