package com.beour.global.jwt;

import com.beour.user.dto.CustomUserDetails;
import com.beour.user.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

  private final JWTUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String authorization = request.getHeader("Authorization");

    if (authorization == null || !authorization.startsWith("Bearer ")) {

      log.info("token null");
      filterChain.doFilter(request, response);

      return;
    }

    String token = authorization.split(" ")[1];

    if (jwtUtil.isExpired(token)) {
      log.info("token expired");
      filterChain.doFilter(request, response);

      return;
    }

    String loginId = jwtUtil.getLoginId(token);
    String role = jwtUtil.getRole(token);

    User user = User.fromJwt(loginId, "temppassword", role);
    CustomUserDetails customUserDetails = new CustomUserDetails(user);

    Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
        customUserDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authToken);

    filterChain.doFilter(request, response);
  }
}