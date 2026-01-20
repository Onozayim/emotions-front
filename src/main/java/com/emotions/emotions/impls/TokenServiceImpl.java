package com.emotions.emotions.impls;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emotions.emotions.entities.Token;
import com.emotions.emotions.entities.User;
import com.emotions.emotions.repositories.TokenRepository;
import com.emotions.emotions.repositories.UserRepository;
import com.emotions.emotions.services.TokenService;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    public String createToken(String email, Duration duration) throws Exception {
        String tokenString = UUID.randomUUID().toString();
        LocalDateTime expDateTime = LocalDateTime.now().plus(duration);

        Optional<User> user =  userRepository.findOneByEmail(email);

        if(user.isPresent())
            return "error";

        Optional<Token> previousToken = tokenRepository.findPreviousToken(email);
        System.out.println("TOKEN");

        if(previousToken.isPresent()) {
            System.out.println("TOKEN FOUND");
            System.out.println(previousToken.get());
            previousToken.get().setUsed(true);
            tokenRepository.save(previousToken.get());
        }

        Token token = new Token(tokenString, email, null, expDateTime, false);
        tokenRepository.save(token);

        return tokenString;
    }
}
