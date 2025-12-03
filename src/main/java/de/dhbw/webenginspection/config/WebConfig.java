package de.dhbw.webenginspection.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.servlet.MultipartConfigElement;

@Configuration
public class WebConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            factory.addConnectorCustomizers(connector -> {
                connector.setMaxPostSize(104857600); // 100MB
                connector.setMaxSavePostSize(104857600); // 100MB
            });
        };
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement("", 104857600, // maxFileSize (100MB)
                104857600, // maxRequestSize (100MB)
                104857600 // fileSizeThreshold (100MB)
        );
    }
}