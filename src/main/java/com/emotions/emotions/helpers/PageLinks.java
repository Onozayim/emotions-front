package com.emotions.emotions.helpers;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;

@Component("pageLinks")
public class PageLinks {

    public String to(int page) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

        return UriComponentsBuilder.fromPath(request.getRequestURI())
                .query(request.getQueryString())
                .replaceQueryParam("page", page)
                .build()
                .toUriString();
    }
}
