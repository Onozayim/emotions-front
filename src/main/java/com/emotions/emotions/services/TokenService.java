package com.emotions.emotions.services;

import java.time.Duration;

public interface TokenService {
    public String createToken(String email, Duration duration) throws Exception;
}
