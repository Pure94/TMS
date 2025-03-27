package pureapps.tms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TimeManagmentSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimeManagmentSystemApplication.class, args);

    }

    // TEMPORARY BEAN TO HASH A PASSWORD ON STARTUP
/*    @Bean
    CommandLineRunner hashPassword(PasswordEncoder passwordEncoder) {
        return args -> {
            String rawPassword = "adminpassword";
            String encodedPassword = passwordEncoder.encode(rawPassword);
            System.out.println("--- Temporary Password Hash ---");
            System.out.println("Raw Password: " + rawPassword);
            System.out.println("BCrypt Hash : " + encodedPassword);
            System.out.println("--- Use this hash in your database for an ADMIN user ---");
        };
    }*/

}
