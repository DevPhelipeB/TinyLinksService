package br.com.dev.tiny.service.urlshortener.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateLinkRequest(
    @NotBlank(message = "Original URL is required")
    @Pattern(regexp = "^(http|https)://.+$", message = "Must start with http:// or https://")
    String originalUrl
) {}
