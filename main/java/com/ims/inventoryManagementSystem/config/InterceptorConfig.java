package com.ims.inventoryManagementSystem.config;
//
//import com.ims.inventoryManagementSystem.interceptor.RequestInterceptor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class InterceptorConfig implements WebMvcConfigurer {
//
//    @Autowired
//    RequestInterceptor requestInterceptor;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(requestInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns(
//                        "/auth/**",
//                        "/inventoryManagementSystem_war/auth/**",
//                        "/css/**",
//                        "/js/**",
//                        "/images/**",
//                        "/index1.html",
//                        "/index.html",
//                        "/"
//                );
//    }
//}



import com.ims.inventoryManagementSystem.interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private RequestInterceptor requestInterceptor;

//    @Autowired
//    private RoleInterceptor roleInterceptor;

    /**
     * addInterceptord method of WenMvcConfigurer interface to register the interceptor.
     * @param registry InterceptorRegistry
     * registering the interceptor for the path patterns.
     *registering the interceptor for checking the role
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor)
                .addPathPatterns("/crm/user/**", "/crm/lead/**","/crm/error/**","/crm/history/**")
                .excludePathPatterns("/crm/user/sign-in", "/crm/user/sign-up", "/crm/user/forget");

//        registry.addInterceptor(roleInterceptor)
//                .addPathPatterns("/crm/user/**")
//                .excludePathPatterns("/crm/user/sign-in", "/crm/user/sign-up", "/crm/user/forget");

    }
}