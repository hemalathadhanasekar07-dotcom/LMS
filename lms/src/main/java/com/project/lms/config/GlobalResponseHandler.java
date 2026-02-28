package com.project.lms.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    private final MessageSource messageSource;

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        if (body instanceof Map<?, ?> map && map.containsKey("message")) {

            Object messageObj = map.get("message");

            if (messageObj instanceof String messageKey) {

                try {
                    String resolvedMessage = messageSource.getMessage(
                            messageKey,
                            null,
                            LocaleContextHolder.getLocale()
                    );

                    return map.entrySet()
                            .stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    e -> e.getKey().equals("message")
                                            ? resolvedMessage
                                            : e.getValue()
                            ));
                } catch (Exception ignored) {
                    return body;
                }
            }
        }

        return body;
    }
}