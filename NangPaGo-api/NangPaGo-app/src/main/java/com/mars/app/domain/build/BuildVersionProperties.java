package com.mars.app.domain.build;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@PropertySource("classpath:META-INF/build-info.properties")
@ConfigurationProperties(prefix = "build")
public class BuildVersionProperties {

    private String version;
}
