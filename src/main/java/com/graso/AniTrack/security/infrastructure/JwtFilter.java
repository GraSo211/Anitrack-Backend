package com.graso.anitrack.security.infrastructure;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.graso.anitrack.security.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        if(authorization == null || !authorization.startsWith("Bearer ")) {
            log.error("No JWT Token found in request headers");
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorization.substring(7);

        boolean isTokenExpired = jwtService.isTokenExpired(token);
        boolean canBeRefreshed = jwtService.canTokenBeRefreshed(token);
        if(isTokenExpired &&  !canBeRefreshed){ 
            log.warn("JWT Token is expired");
            filterChain.doFilter(request, response);
            return;
        }


        String username = jwtService.getUsername(token);

        if(username == null && SecurityContextHolder.getContext().getAuthentication() == null){
            log.warn("Invalid token or user already authenticanted");
            filterChain.doFilter(request, response);
            return;
        }
        log.info("JWT Token found: {}", token);
        UserDetails userDetails = User.withDefaultPasswordEncoder()
                .username(username)
                .password("password")
                .roles("USER")
                .build();

        if(isTokenExpired && canBeRefreshed){
            log.info("JWT Token is expired but can be refreshed");
            token = jwtService.renewToken(token, userDetails);
            response.setHeader("Authorization", "Bearer " + token);
        }

        
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

}
