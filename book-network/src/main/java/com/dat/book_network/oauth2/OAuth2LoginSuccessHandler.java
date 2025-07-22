package com.dat.book_network.oauth2;

import com.dat.book_network.role.Role;
import com.dat.book_network.security.JwtService;
import com.dat.book_network.user.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final String frontURl;

    public OAuth2LoginSuccessHandler(JwtService jwtService,
                                     @Value("${application.frontends.url}")String frontURl) {
        this.jwtService = jwtService;
        this.frontURl = frontURl;
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User account = oAuth2User.getAccount();
        var claims = new HashMap<String, Object>();
        claims.put("fullName", account.fullName());
        var jwtToken = jwtService.generateToken(claims, account);

        Role role = account.getRoles().get(0);


        String path = "/";
        String redirectUrl = String.format(frontURl+"%s?token=%s&role=%s", path, jwtToken, role);
        response.sendRedirect(redirectUrl);
    }
}
