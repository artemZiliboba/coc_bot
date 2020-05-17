package com.home.server.service;

import com.home.server.model.developer.CocToken;

public interface IAuthService {
    CocToken getCocToken(String email, String pass);
}
