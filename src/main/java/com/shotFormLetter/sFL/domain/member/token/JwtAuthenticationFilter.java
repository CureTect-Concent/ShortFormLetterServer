package com.shotFormLetter.sFL.domain.member.token;

//import com.shotFormLetter.sFL.ExceptionHandler.TokenExpiredException;
import lombok.RequiredArgsConstructor;
        import org.springframework.security.core.Authentication;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

        import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
        import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        // 토큰이 유효하다면
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰으로부터 유저 정보를 받아
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // SecurityContext 에 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 Filter 실행
        chain.doFilter(request, response);
    }


//        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
//            // 토큰이 유효하다면
//        if (token != null && jwtTokenProvider.validateToken(token)) {
//              // 토큰으로부터 유저 정보를 받아
//            Authentication authentication = jwtTokenProvider.getAuthentication(token);
//            // SecurityContext 에 객체 저장
//               SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//        chain.doFilter(request,response);

//        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
//        if (token != null && jwtTokenProvider.validateToken(token)) {
//                // 토큰으로부터 유저 정보를 받아
//                Authentication authentication = jwtTokenProvider.getAuthentication(token);
//                // SecurityContext 에 객체 저장
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//        } else if(token !=null && jwtTokenProvider.validateToken(token)==Boolean.FALSE){
//            throw new TokenExpiredException("토큰 만료");
//        }
//        chain.doFilter(request, response);
//        try {
//            // 헤더에서 토큰 받아오기
//            String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
//            // 토큰이 유효하다면
//            if (token != null && jwtTokenProvider.validateToken(token)) {
//                // 토큰으로부터 유저 정보를 받아
//                Authentication authentication = jwtTokenProvider.getAuthentication(token);
//                // SecurityContext 에 객체 저장
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
////            else if(token !=null && jwtTokenProvider.validateToken(token)==Boolean.FALSE) {
////                throw new IllegalStateException("토큰 만료");
////            }
//            chain.doFilter(request, response);
//        } catch (TokenExpiredException ex) {
//            // 토큰 만료 예외 처리
//            throw new TokenExpiredException("액세스 토큰 만료");
//        }


}
