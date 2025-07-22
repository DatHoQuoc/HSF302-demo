package com.dat.book_network.oauth2;

import com.dat.book_network.exception.AccountDisabledException;
import com.dat.book_network.role.Role;
import com.dat.book_network.role.RoleRepository;
import com.dat.book_network.user.User;
import com.dat.book_network.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        //extract user information
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");



        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        User account = findOrCreateAccount(email, name, registrationId);

        return new CustomOAuth2User(oAuth2User, account, userNameAttributeName);
    }

    @Transactional
    protected User findOrCreateAccount(String email, String name, String registrationId) {

        Optional<User> existingAccount = accountRepository.findByEmail(email);
        var userRole = roleRepository.findByName("USER")
                // todo - better exception handling
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initialized"));
        if(existingAccount.isPresent()) {
            User account = existingAccount.get();

            if (!account.isEnabled() || account.isAccountLocked()) {
                throw new AccountDisabledException(
                        "Account access denied",
                        email,
                        account.isAccountLocked(),
                        !account.isEnabled()
                );
            }
            account.setLastModifiedDate(LocalDateTime.now());

            if(account.getEmail() == null) {
                 account = User.builder()
                        .firstName(name)
                         .roles(List.of(userRole))
                        .build();

            }

            return accountRepository.save(account);
        }

        // Create new account with patient profile
        User newAccount = User.builder()
                .email(email)
                .firstName(name)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .enabled(true)
                .accountLocked(false)
                .roles(List.of(userRole))
                .lastModifiedDate(LocalDateTime.now())
                .build();



        return accountRepository.save(newAccount);
    }


    private String generatePatientId() {
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        int random = new Random().nextInt(900) + 100; // random 3-digit number
        return "PAT-" + timestamp + "-" + random;
    }
}
