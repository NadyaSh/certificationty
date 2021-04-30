package org.niitp.config;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by IntelliJ IDEA.
 * User: zimin
 * Date: 09.06.2018
 * Time: 18:00
 */
@Configuration
public class JacksonConfig {
    @Bean
    public JtsModule jtsModule() {
        return new JtsModule();
    }
}
