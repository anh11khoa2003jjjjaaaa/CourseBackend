//package org.example.sellingcourese;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//
//@EntityScan(basePackages = {"org.example.sellingcourese.Model", "another.package"})
//@SpringBootApplication
//public class SellingCoureseApplication {
//    public static void main(String[] args) {
//        SpringApplication.run(SellingCoureseApplication.class, args);
//
//
//
//    }
//
//}
package org.example.sellingcourese;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.example.sellingcourese.Service.CourseService;

@EntityScan(basePackages = {"org.example.sellingcourese.Model", "another.package"})
@SpringBootApplication
public class SellingCoureseApplication {
    public static void main(String[] args) {
        // Khởi chạy ứng dụng Spring Boot và lấy context
        ConfigurableApplicationContext context = SpringApplication.run(SellingCoureseApplication.class, args);

        // Lấy bean CourseService từ context
        CourseService courseService = context.getBean(CourseService.class);

        // Gọi phương thức getPathToGoogleCredentials()
        String path = courseService.getPathToGoogleCredentials();
        System.out.println("Google credentials path: " + path);
    }
}