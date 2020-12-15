package com.metaShare.common.security;


import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;

import com.metaShare.common.utils.JwtUtil;
import com.metaShare.common.utils.NetWorkUtil;
import com.metaShare.common.utils.StringUtils;
import io.jsonwebtoken.*;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.crypto.SecretKey;
import javax.naming.AuthenticationException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;


public class JwtFilter extends BasicHttpAuthenticationFilter {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 前置处理
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
    /**
     * 后置处理
     */
    @Override
    protected void postHandle(ServletRequest request, ServletResponse response) {
        // 添加跨域支持
        this.fillCorsHeader(WebUtils.toHttp(request), WebUtils.toHttp(response));
    }
    /**
     * 过滤器拦截请求的入口方法
     * 返回 true 则允许访问
     * 返回false 则禁止访问，会进入 onAccessDenied()
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // 原用来判断是否是登录请求，在本例中不会拦截登录请求，用来检测Header中是否包含 JWT token 字段
        if (this.isLoginRequest(request, response))
            return false;
        boolean allowed = false;
        try {
            // 检测Header里的 JWT token内容是否正确，尝试使用 token进行登录
            allowed = executeLogin(request, response);

        } catch (IllegalStateException e) { // not found any token
            log.error("Not found any token");
        } catch (Exception e) {
            log.error("Error occurs when login", e);
        }
        return allowed || super.isPermissive(mappedValue);
    }
    /**
     * 检测Header中是否包含 JWT token 字段
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        return ((HttpServletRequest) request).getHeader(SecurityConsts.REQUEST_AUTH_HEADER) == null;
    }
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader(SecurityConsts.REQUEST_AUTH_HEADER);
        if(!StringUtils.isEmpty(authorization)){
            SecretKey key = JwtUtil.generateKey(SignatureAlgorithm.HS256, "wjtree.xin");
            // 移除 JWT 前的"Bearer "字符串
            String JWT_SEPARATOR ="Bearer ";
            String claimsJws = org.apache.commons.lang3.StringUtils.substringAfter(authorization, JWT_SEPARATOR);
            // 解析 JWT 字符串
            Jws<Claims> jws = Jwts.parser().setSigningKey(key).parseClaimsJws(claimsJws);
            JSONObject json = JSONObject.parseObject(jws.getBody().getSubject());
            long l = System.currentTimeMillis();
            Integer exp = json.getIntValue("exp");
            if(exp>l){
                return  false;
            }
            return onLoginSuccess(null, null, request, response);
//            AuthenticationToken token = createToken(request, response);
//            return onLoginSuccess(token, null, request, response);

        }

//         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//        Date d1 = claims1.getIssuedAt();
//        Date d2 = claims1.getExpiration();
//        System.out.println("username参数值：" + claims1.get("username"));
//        System.out.println("登录用户的id：" + claims1.getId());
//        System.out.println("登录用户的名称：" + claims1.getSubject());
//        System.out.println("令牌签发时间：" + sdf.format(d1));
//        System.out.println("令牌过期时间：" + sdf.format(d2));


        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }
    /**
     * 身份验证,检查 JWT token 是否合法
     */
//    @Override
//    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
//        AuthenticationToken token = createToken(request, response);
//        if (token == null) {
//            String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken "
//                    + "must be created in order to execute a login attempt.";
//            throw new IllegalStateException(msg);
//        }
//        try {
//            Subject subject = getSubject(request, response);
//            subject.login(token); // 交给 Shiro 去进行登录验证
//            return onLoginSuccess(token, subject, request, response);
//        } catch (AuthenticationException e) {
//            return onLoginFailure(token, e, request, response);
//        }
//    }
    /**
     * isAccessAllowed()方法返回false，会进入该方法，表示拒绝访问
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletResponse httpResponse = WebUtils.toHttp(servletResponse);
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json;charset=UTF-8");
        httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        PrintWriter writer = httpResponse.getWriter();
        writer.write("{\"errCode\": 401, \"msg\": \"UNAUTHORIZED\"}");
        fillCorsHeader(WebUtils.toHttp(servletRequest), httpResponse);
        return false;
    }
    /**
     * Shiro 利用 JWT token 登录成功，会进入该方法
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
                                     ServletResponse response) throws Exception {
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader(SecurityConsts.REQUEST_AUTH_HEADER);
        httpResponse.setHeader(SecurityConsts.REQUEST_AUTH_HEADER, getToken(authorization));
        return true;
    }

    /**
     * 获取token
     * @return
     */
    private String getToken(String token) {
        String userId  =JwtUtil.getUserId(token);
        String username  =JwtUtil.getUserName(token);

        JSONObject json = new JSONObject();
        json.put("userId", userId);
        json.put("username",username);

        return JwtUtil.buildJWT(json.toJSONString());
    }
    /**
     * Shiro 利用 JWT token 登录失败，会进入该方法
     */
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request,
                                     ServletResponse response) {
        // 此处直接返回 false ，交给后面的  onAccessDenied()方法进行处理
        return false;
    }




    /**
     * 添加跨域支持
     */
    protected void fillCorsHeader(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,HEAD");
        httpServletResponse.setHeader("Access-Control-Allow-Headers",
                httpServletRequest.getHeader("Access-Control-Request-Headers"));
    }

}
