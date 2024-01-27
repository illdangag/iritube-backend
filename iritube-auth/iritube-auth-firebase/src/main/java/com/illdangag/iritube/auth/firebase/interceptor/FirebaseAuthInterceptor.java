package com.illdangag.iritube.auth.firebase.interceptor;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
@Component
public class FirebaseAuthInterceptor implements HandlerInterceptor {
    private final FirebaseApp firebaseApp;

    public FirebaseAuthInterceptor(@Value("${auth.firebase.config:#{null}}") String configPath) {
        try {
            File file = new File(configPath);
            InputStream inputStream = new FileInputStream(file);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .build();
            firebaseApp = FirebaseApp.initializeApp(options);
        } catch (Exception exception) {
            throw new RuntimeException(exception); // TODO
        }
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        } else {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }
    }

//    private Optional<FirebaseToken> getFirebaseToken(HttpServletRequest request) throws IricomException {
//        String authorization = request.getHeader("Authorization");
//
//        if (authorization == null || !authorization.startsWith("Bearer ")) {
//            return Optional.empty();
//        }
//
//        String token = authorization.substring(7);
//        FirebaseToken firebaseToken;
//        try {
//            FirebaseAuth firebaseAuth = this.firebaseInitializer.getFirebaseAuth();
//            firebaseToken = firebaseAuth.verifyIdToken(token);
//        } catch (FirebaseAuthException exception) {
//            if (exception.getAuthErrorCode().name().equals("EXPIRED_ID_TOKEN")) {
//                // 토큰 만료
//                throw new IricomException(IricomErrorCode.EXPIRED_FIREBASE_ID_TOKEN);
//            } else {
//                throw new IricomException(IricomErrorCode.INVALID_FIREBASE_ID_TOKEN);
//            }
//        } catch (Exception exception) {
//            throw new IricomException(IricomErrorCode.INVALID_FIREBASE_ID_TOKEN);
//        }
//
//        return Optional.of(firebaseToken);
//    }
}
