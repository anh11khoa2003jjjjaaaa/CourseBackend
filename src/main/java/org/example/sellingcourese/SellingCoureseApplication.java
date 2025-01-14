package org.example.sellingcourese;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
@EntityScan(basePackages = {"org.example.sellingcourese.Model", "another.package"})
@SpringBootApplication
public class SellingCoureseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SellingCoureseApplication.class, args);
    }

}
