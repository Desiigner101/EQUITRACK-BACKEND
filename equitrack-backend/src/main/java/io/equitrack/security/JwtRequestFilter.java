package io.equitrack.security;

import io.equitrack.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j  // ✅ ADD THIS
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;

        log.info("🔍 Processing request: {} {}", request.getMethod(), request.getRequestURI()); // ✅ ADD THIS
        log.info("🔍 Auth Header: {}", authHeader != null ? "Present" : "Missing"); // ✅ ADD THIS

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            jwt = authHeader.substring(7);
            try {
                email = jwtUtil.extractUsername(jwt);
                log.info("✅ Extracted email from token: {}", email); // ✅ ADD THIS
            } catch (Exception e) {
                log.error("❌ Failed to extract email from token: {}", e.getMessage()); // ✅ ADD THIS
            }
        }

        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

            if(jwtUtil.validateToken(jwt, userDetails)){
                log.info("✅ Token validated successfully for: {}", email); // ✅ ADD THIS

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                log.error("❌ Token validation failed for: {}", email); // ✅ ADD THIS
            }
        }

        filterChain.doFilter(request, response);
    }
}