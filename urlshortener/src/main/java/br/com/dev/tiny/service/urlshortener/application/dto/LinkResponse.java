package br.com.dev.tiny.service.urlshortener.application.dto;

import br.com.dev.tiny.service.urlshortener.domain.entity.Link;

import java.util.Locale;

public record LinkResponse(
    String code,
    String shortUrl,
    String originalUrl,
    long visits
) {
    public static LinkResponse fromEntity(Link link, String baseUrl) {
        String shortUrl = String.format(Locale.ROOT, "%s/tinyapp/r/%s", baseUrl, link.getCode());
        return new LinkResponse(link.getCode(), shortUrl, link.getOriginalUrl(), link.getVisits());
    }
}
