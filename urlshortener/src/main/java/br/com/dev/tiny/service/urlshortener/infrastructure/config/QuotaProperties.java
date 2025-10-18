package br.com.dev.tiny.service.urlshortener.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tinylinks.quota")
public class QuotaProperties {

    private int perUserPerDay = 200;

    public int getPerUserPerDay() {
        return perUserPerDay;
    }

    public void setPerUserPerDay(int perUserPerDay) {
        this.perUserPerDay = perUserPerDay;
    }
}
