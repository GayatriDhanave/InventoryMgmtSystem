//package com.ims.inventoryManagementSystem.config;
//
////import com.ims.inventoryManagementSystem.interceptor.RequestInterceptor;
//import com.ims.inventoryManagementSystem.interceptor.RequestInterceptor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
////import org.springframework.security.crypto.bcrypt.BCrypt;
////import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
////import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class InterceptorConfig implements WebMvcConfigurer {
//
//    @Autowired
//    RequestInterceptor requestInterceptor;
//
//    @Override
//    public void addInterceptors (InterceptorRegistry registry) {
//        registry.addInterceptor(requestInterceptor).addPathPatterns("/users");
//    }
//
////    @Override
////    public void addResourceHandlers (ResourceHandlerRegistry registry) {
////        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
////    }
//
////    @Bean
////    public BCryptPasswordEncoder passwordEncoder() {
////        return new BCryptPasswordEncoder();
////    }
//}
