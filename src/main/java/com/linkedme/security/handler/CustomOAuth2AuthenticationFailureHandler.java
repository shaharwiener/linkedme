package com.linkedme.security.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomOAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

//    @Override
//    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
//                                        AuthenticationException exception) throws IOException, ServletException {
//        // Log the full exception details
//        System.err.println("Authentication failed: " + exception.getMessage());
//        exception.printStackTrace();
//
//        // You can also redirect the user to a custom error page
//        response.sendRedirect("/error?message=" + exception.getMessage());
//    }

    @Override
    public void onAuthenticationFailure(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, AuthenticationException exception) throws IOException, jakarta.servlet.ServletException {
        // Log the full exception details
        System.err.println("Authentication failed: " + exception.getMessage());
        exception.printStackTrace();

        // You can also redirect the user to a custom error page
        response.sendRedirect("/error?message=" + exception.getMessage());
    }
}
