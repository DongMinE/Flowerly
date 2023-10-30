package com.ssafy.flowerly.config.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.flowerly.JWT.JWTService;
import com.ssafy.flowerly.config.auth.vo.CustomOAuth2User;
import com.ssafy.flowerly.member.MemberRole;
import com.ssafy.flowerly.util.DataResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${url.test}")
    private String redirectURL;
    private final JWTService jwtService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("OAuth2.0 성공!!!");
        log.info("요청 URI : {}", request.getRequestURI());
        try{
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            //getPrincipal()로 얻은 Member정보가 GEUST인 경우 처음 요청 -> 정보 입력 페이지로 리다이렉트 한다.
            if(oAuth2User.getRole() == MemberRole.GUEST){
                String accessToken = jwtService.createAccessToken(oAuth2User.getMemberId());
                response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
                response.sendRedirect("oauth2/sign-up"); //프론트 회원가입 후 추가 정보 입력 창으로 리다이렉트

                jwtService.sendAccessTokenAndRefreshToken(response, accessToken, null);
            }else{
                loginSuccess(response, oAuth2User);
            }
        }catch (Exception e){
            log.error("OAuth2.0 에러 발생!");
            e.printStackTrace();
        }
    }

    // 추후 과제 : 소설 로그인 시, 무조건 토큰 생성이 아닌 JWT필터링 처럼 RefreshToken 유무에 따라 다르게 처리한다.
    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        log.info("로그인 성공!");
        String accessToken = jwtService.createAccessToken(oAuth2User.getMemberId());
        String refreshToken = jwtService.createRefreshToken(oAuth2User.getMemberId());
        jwtService.sendAccessTokenAndRefreshToken(response, accessToken, refreshToken);
        response.setContentType("application/json;charset=UTF-8");

//        Map<String, String > map = new HashMap<>();
//        map.put("token", accessToken);
//        PrintWriter writer = response.getWriter();
//        writer.println(mapper.writeValueAsString(map));
//        writer.flush();

        response.sendRedirect(redirectURL + "/api/member/jwt-test");
    }
}
