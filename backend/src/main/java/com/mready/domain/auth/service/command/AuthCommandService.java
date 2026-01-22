package com.mready.domain.auth.service.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthCommandService {

    void logout(HttpServletRequest request, HttpServletResponse response);

    void reissue(HttpServletRequest request, HttpServletResponse response);
}
