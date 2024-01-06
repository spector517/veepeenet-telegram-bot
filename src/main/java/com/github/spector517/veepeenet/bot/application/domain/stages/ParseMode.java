package com.github.spector517.veepeenet.bot.application.domain.stages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParseMode {

    @JsonProperty("Markdown")
    MARKDOWN("Markdown"),

    @JsonProperty("MarkdownV2")
    MARKDOWN_V2("MarkdownV2"),

    PLAIN(null);

    private final String type;
}
