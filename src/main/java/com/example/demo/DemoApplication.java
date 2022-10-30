package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    CommandLineRunner run(StudentRepository repository, MongoTemplate mongoTemplate) {
        return args -> {
            Address address = new Address(
                    "Turkey",
                    "Konya",
                    "4200");
            String email = "husna_poyraz@gmail.com";
            Student student = new Student(
                    "Hüsna",
                    "Poyraz",
                    email,
                     Gender.FEMALE,
                     address,
                     List.of("Computer Science", "Machine Learning"),
                     BigDecimal.TEN,
                     LocalDateTime.now()
            );

            repository.findStudentByEmail(email)
                    .ifPresentOrElse(student1 -> {
                        System.out.println(student + " already exists!");
                    }, () -> {
                        System.out.println("Inserting student " + student);
                        repository.insert(student);
                    });

        };

    }

    private void usingMongoTemplateAndQuery(StudentRepository repository, MongoTemplate mongoTemplate, String email, Student student) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));

        List<Student> students = mongoTemplate.find(query, Student.class);

        if (students.size() > 1) {
            throw new IllegalStateException("Found many students with email : " + email);
        }

        if (students.isEmpty()) {
            System.out.println("Inserting student " + student);
            repository.insert(student);
        } else {
            System.out.println(student + " already exists!");
        }
    }

}
