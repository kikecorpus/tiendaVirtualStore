

package com.tienda.virtual_store.security;

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

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService        jwtService;

    // ✅ MEJORA 1: Inyectamos la INTERFAZ UserDetailsService, no la implementación
    // concreta UserDetailsServiceImpl. Esto respeta el principio de inversión de
    // dependencias (DIP) y facilita los tests con mocks.
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest  request,
                                    HttpServletResponse response,
                                    FilterChain         filterChain)
            throws ServletException, IOException {

        // 1. Leer el header Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. Si no hay token o no empieza con "Bearer ", dejar pasar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token (quitar el prefijo "Bearer ")
        String token = authHeader.substring(7);

        // ✅ MEJORA 2: extractEmail ahora retorna null si el token es inválido
        // o está expirado. Esto elimina la llamada doble a parseClaims que había
        // antes (isTokenValid + extractEmail = 2 parseos del mismo token).
        String email = jwtService.extractEmail(token);

        // 4. Si el token era inválido (email null), continuar sin autenticar
        if (email == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Si el usuario no está autenticado aún en este request
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Cargar el usuario desde la DB
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(email);

            // 7. Crear el objeto de autenticación
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // 8. Registrar la autenticación en el contexto de Spring Security
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 9. Continuar con el siguiente filtro
        filterChain.doFilter(request, response);
    }
}