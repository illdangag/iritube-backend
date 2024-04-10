package com.illdangag.iritube.auth.firebase.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.illdangag.iritube.auth.firebase.data.entity.FirebaseAuthentication;
import com.illdangag.iritube.auth.firebase.exception.IritubeFirebaseError;
import com.illdangag.iritube.auth.firebase.repository.FirebaseAuthenticationRepository;
import com.illdangag.iritube.core.annotation.IritubeAuthorization;
import com.illdangag.iritube.core.annotation.IritubeAuthorizationType;
import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.type.AccountAuth;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.core.repository.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Component
public class FirebaseAuthInterceptor implements HandlerInterceptor {
    private final FirebaseAuthenticationRepository firebaseAuthenticationRepository;
    private final AccountRepository accountRepository;
    private final FirebaseApp firebaseApp;

    @Autowired
    public FirebaseAuthInterceptor(@Value("${auth.firebase.config:#{null}}") String configPath,
                                   FirebaseAuthenticationRepository firebaseAuthenticationRepository,
                                   AccountRepository accountRepository) {
        this.firebaseAuthenticationRepository = firebaseAuthenticationRepository;
        this.accountRepository = accountRepository;

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
        }

        Account account = null;

        Optional<FirebaseToken> firebaseTokenOptional = this.getFirebaseToken(request);
        if (firebaseTokenOptional.isPresent()) { // firebase token이 존재하는 경우
            FirebaseToken firebaseToken = firebaseTokenOptional.get();
            String firebaseUid = firebaseToken.getUid();
            String email = firebaseToken.getEmail();

            Optional<FirebaseAuthentication> firebaseAuthenticationOptional = this.firebaseAuthenticationRepository.getFirebaseAuthentication(firebaseUid);
            if (firebaseAuthenticationOptional.isEmpty()) { // 이전에 로그인한 적이 없는 계정
                account = Account.builder()
                        .nickname(email)
                        .build();
                this.accountRepository.save(account);
                FirebaseAuthentication firebaseAuthentication = FirebaseAuthentication.builder()
                        .id(firebaseUid)
                        .account(account)
                        .build();
                this.firebaseAuthenticationRepository.save(firebaseAuthentication);
            } else {
                FirebaseAuthentication firebaseAuthentication = firebaseAuthenticationOptional.get();
                account = firebaseAuthentication.getAccount();
            }

            request.setAttribute("account", account); // 계정 정보를 api endpoint로 전달
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        IritubeAuthorization iritubeAuthorization = handlerMethod.getMethodAnnotation(IritubeAuthorization.class);

        if (iritubeAuthorization == null) { // 권한 annotation이 설정 되어 있지 않은 경우
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        IritubeAuthorizationType[] authorizationTypes = iritubeAuthorization.type();
        List<IritubeAuthorizationType> authorizationTypeList = authorizationTypes == null ? Collections.emptyList() : Arrays.asList(authorizationTypes);

        if (authorizationTypeList.isEmpty() || authorizationTypeList.contains(IritubeAuthorizationType.NONE)) { // 설정된 권한 목록이 존재하지 않거나, 권한이 팔요하지 않은 경우
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        if (firebaseTokenOptional.isEmpty()) {
            throw new IritubeException(IritubeFirebaseError.NOT_EXIST_FIREBASE_ID_TOKEN);
        }

        if (authorizationTypeList.contains(IritubeAuthorizationType.SYSTEM_ADMIN) && !account.getAuth().equals(AccountAuth.SYSTEM_ADMIN)) { // 관리자 권한이 필요하지만 계정이 관리자가 아닌 경우
            throw new IritubeException(IritubeCoreError.INVALID_AUTHORIZATION);
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    public Optional<FirebaseToken> getFirebaseToken(HttpServletRequest request) throws IritubeException {
        String authorization = request.getHeader("Authorization");

        // firebase 토큰이 존재하지 않는 경우
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Optional.empty();
        }

        String token = authorization.substring(7);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(this.firebaseApp);
        FirebaseToken firebaseToken;
        try {
            firebaseToken = firebaseAuth.verifyIdToken(token);
        } catch (FirebaseAuthException exception) {
            if (exception.getAuthErrorCode().name().equals("EXPIRED_ID_TOKEN")) {
                // 토큰 만료
                throw new IritubeException(IritubeFirebaseError.EXPIRED_FIREBASE_ID_TOKEN);
            } else {
                throw new IritubeException(IritubeFirebaseError.INVALID_FIREBASE_ID_TOKEN);
            }
        } catch (Exception exception) {
            throw new IritubeException(IritubeFirebaseError.INVALID_FIREBASE_ID_TOKEN, exception);
        }

        return Optional.of(firebaseToken);
    }
}
