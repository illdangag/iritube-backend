package com.illdangag.iritube.server.configuration;

import com.illdangag.iritube.core.annotation.RequestContext;
import com.illdangag.iritube.core.data.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
public class RequestContextResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        RequestContext requestContext = methodParameter.getParameter().getAnnotation(RequestContext.class);
        if (requestContext == null) {
            return false;
        }

        Class<?> targetClass = methodParameter.getParameterType();
        return targetClass.equals(Account.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory binderFactory) throws Exception {
        Class targetClass = methodParameter.getParameterType();
        if (targetClass.equals(Account.class)) {
            return nativeWebRequest.getAttribute("account", 0);
        }
        return null;
    }
}
