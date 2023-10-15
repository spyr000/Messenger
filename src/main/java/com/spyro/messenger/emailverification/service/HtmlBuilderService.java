package com.spyro.messenger.emailverification.service;

import com.spyro.messenger.emailverification.util.Link;

public interface HtmlBuilderService {
    HtmlBuilderService htmlContent(String htmlContent);

    HtmlBuilderService name(String name);

    HtmlBuilderService message(String message);

    HtmlBuilderService link(String text, String url);

    HtmlBuilderService link(Link link);

    HtmlBuilderService links(Link... links);

    HtmlBuilderService company(String company);

    String build();
}
