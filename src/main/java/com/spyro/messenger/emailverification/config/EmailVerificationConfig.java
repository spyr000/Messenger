package com.spyro.messenger.emailverification.config;


import com.spyro.messenger.exceptionhandling.exception.BaseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.function.Function;

@Configuration
public class EmailVerificationConfig {
    @Bean
    public Function<Resource,String> htmlReader(Charset charset) {
        return resource -> {
            try {
                return resource.getContentAsString(charset);
            } catch (IOException e) {
                //TODO: make exception class
                throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        };
    }

    @Bean
    public Charset charset() {
        return Charset.defaultCharset();
    }


}
