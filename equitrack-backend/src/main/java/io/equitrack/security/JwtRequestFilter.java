package io.equitrack.security;

import io.equitrack.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter - The Security Gatekeeper
 *
 * This filter intercepts EVERY incoming HTTP request and checks for JWT tokens
 * It's the bouncer at the door of your application - validating IDs before letting people in
 */
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService; // Knows how to load user data from database
    private final JwtUtil jwtUtil;                       // Knows how to handle JWT tokens (create/validate)

    /**
     * THE MAIN FILTER METHOD - Runs on every single request
     * This is where the security magic happens
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Step 1: Check if request has Authorization header with Bearer token
        final String authHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;

        // Step 2: Extract JWT token from "Bearer <token>" format
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            jwt = authHeader.substring(7); // Remove "Bearer " prefix to get pure token
            email = jwtUtil.extractUsername(jwt); // Decode token to get user email
        }

        // Step 3: If we found a user email AND no authentication exists in current context
        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){

            // Step 4: Load user details from database using the email
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

            // Step 5: Validate the JWT token against the user details
            if(jwtUtil.validateToken(jwt, userDetails)){

                // Step 6: Create Spring Security authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,           // The user principal
                        null,                  // Credentials (null because we use tokens)
                        userDetails.getAuthorities() // User roles/permissions
                );

                // Step 7: Add request details (IP, session ID, etc.) to authentication
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Step 8: Set the authentication in Security Context - USER IS NOW AUTHENTICATED!
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Step 9: Continue with the next filter in the chain
        // If authentication failed or no token, request continues as unauthenticated
        filterChain.doFilter(request, response);
    }
}