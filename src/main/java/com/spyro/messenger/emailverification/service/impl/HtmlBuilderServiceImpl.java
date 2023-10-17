package com.spyro.messenger.emailverification.service.impl;

import com.spyro.messenger.emailverification.service.HtmlBuilderService;
import com.spyro.messenger.emailverification.misc.Link;
import org.springframework.stereotype.Service;

@Service
public class HtmlBuilderServiceImpl implements HtmlBuilderService {
    private static final String NAME_LABEL = "[[name]]";
    private static final String MESSAGE_LABEL = "[[message]]";
    private static final String COMPANY_LABEL = "[[company]]";
    private static final String LINK_LABEL = "[[link]]";
    private String htmlContent;

    @Override
    public HtmlBuilderService htmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
        return this;
    }

    @Override
    public HtmlBuilderService name(String name) {
        htmlContent = htmlContent.replace(NAME_LABEL, name);
        return this;
    }

    @Override
    public HtmlBuilderService message(String message) {
        htmlContent = htmlContent.replace(MESSAGE_LABEL, message);
        return this;
    }

    @Override
    public HtmlBuilderService link(String text, String url) {
        htmlContent = htmlContent.formatted(Link.builder().text(text).url(url).build().toString());
        return this;
    }

    @Override
    public HtmlBuilderService link(Link link) {
        htmlContent = htmlContent.formatted(link.toString());
        return this;
    }

    @Override
    public HtmlBuilderService links(Link... links) {
        for (var link : links) {
            htmlContent = htmlContent.replace(LINK_LABEL, link.toString());
        }
        return this;
    }

    @Override
    public HtmlBuilderService company(String company) {
        htmlContent = htmlContent.replace(COMPANY_LABEL, company);
        return this;
    }

    @Override
    public String build() {
        var result = htmlContent.replace(LINK_LABEL, "");
        htmlContent = null;
        return result;
    }
}
