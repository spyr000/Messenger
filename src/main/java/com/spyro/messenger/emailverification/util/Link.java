package com.spyro.messenger.emailverification.util;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Link {
    private static final String LINK_PATTERN = "<h3><a href=\"%s\" target=\"_self\">%s</a></h3>";
    private String text;
    private String url;

    @Override
    public String toString() {
        return LINK_PATTERN.formatted(url, text) + "[[link]]";
    }
}
