package pl.edu.atar.recruitment;

import io.camunda.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Deployment(resources = "classpath*:/model/*.*")
public class RecruitmentProcessApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecruitmentProcessApplication.class, args);
    }
}