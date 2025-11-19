//package com.ims.inventoryManagementSystem.config;
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
