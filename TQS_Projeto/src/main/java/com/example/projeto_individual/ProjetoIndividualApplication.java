package com.example.projeto_individual;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@Controller
public class ProjetoIndividualApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjetoIndividualApplication.class, args);
    }

    @RequestMapping("/")
    public String home()  { return "/index"; }
}
