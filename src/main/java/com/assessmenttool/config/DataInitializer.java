package com.assessmenttool.config;

import com.assessmenttool.model.Assessment;
import com.assessmenttool.model.Report;
import com.assessmenttool.model.Submission;
import com.assessmenttool.model.User;
import com.assessmenttool.repository.AssessmentRepository;
import com.assessmenttool.repository.ReportRepository;
import com.assessmenttool.repository.SubmissionRepository;
import com.assessmenttool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("Initializing default MySQL database records with BCrypt hashed passwords...");

            // Super Admin
            userRepository.save(new User("superadmin-1", "purusothaman", "purusothamanp23@gmail.com", passwordEncoder.encode("purusothamanp@23"), "admin", "active", "2026-09-28 19:30", null, null));

            // Default Users
            userRepository.save(new User("admin-demo-1", "Administrator", "admin@gmail.com", passwordEncoder.encode("admin123"), "admin", "active", "2026-09-28 18:00", null, null));
            userRepository.save(new User("educator-demo-1", "Prof. Smith", "educator@gmail.com", passwordEncoder.encode("educator123"), "educator", "active", "2026-09-28 18:00", null, null));
            userRepository.save(new User("student-demo-1", "John Doe", "student@gmail.com", passwordEncoder.encode("student123"), "student", "active", "2026-09-28 18:00", "2004-10-30", "st001"));
            userRepository.save(new User("t5FlZ_iaC0I", "educator2", "educator2@gmail.com", passwordEncoder.encode("educator2@2"), "educator", "active", "2026-06-26 17:05", null, null));
            userRepository.save(new User("G7GWnf_NCCA", "admin2", "admin2@gmail.com", passwordEncoder.encode("admin2@2"), "admin", "active", "2026-06-27 20:23", null, null));
            userRepository.save(new User("xFyioXkLilY", "student1", "student1@gmail.com", passwordEncoder.encode("student1"), "student", "active", "2026-08-20 10:30", "2004-10-30", "st002"));
            userRepository.save(new User("c-nCk7aGpBc", "student2", "student2@gmail.com", passwordEncoder.encode("student2"), "student", "active", "2026-04-19 12:39", "2004-10-30", "st003"));
            userRepository.save(new User("pNkvHgyDf_g", "student3", "student3@gmail.com", passwordEncoder.encode("student3"), "student", "active", "2026-04-19 12:41", "2004-10-30", "st004"));

            // Sample Assessment
            Assessment sample = new Assessment("demo-ass-1", "Solar System Quiz", "Quiz", "Astronomy", "Science", "2026-09-28", "educator-demo-1");
            sample.setQuestionFormats(List.of("multipleChoice"));
            sample.setQuestions(List.of(
                    Map.of(
                            "id", 1001,
                            "text", "What is the average distance from Earth to the Sun?",
                            "type", "MCQ",
                            "options", List.of("100 million km", "149.6 million km", "200 million km", "50 million km"),
                            "correctAnswer", 1
                    ),
                    Map.of(
                            "id", 1002,
                            "text", "Which planet is the largest in our solar system?",
                            "type", "MCQ",
                            "options", List.of("Mars", "Earth", "Jupiter", "Saturn"),
                            "correctAnswer", 2
                    )
            ));
            assessmentRepository.save(sample);

            // Sample Report
            reportRepository.save(new Report("rep-1", "Solar System Quiz Overview", 12, "2026-09-28", 85.0, 78.5, 100.0));

            // Sample Submission
            Submission sub = new Submission("sub-1", "student1", "demo-ass-1", "Solar System Quiz", 100, "Passed", "2026-09-28");
            sub.setAnswers(Map.of("1001", 1, "1002", 2));
            submissionRepository.save(sub);

            System.out.println("Default records successfully inserted into MySQL.");
        } else {
            // Automatic migration for existing plain text passwords in the database
            List<User> existingUsers = userRepository.findAll();
            int migratedCount = 0;
            for (User user : existingUsers) {
                String pwd = user.getPassword();
                if (pwd != null && !pwd.isBlank() && !pwd.startsWith("$2a$") && !pwd.startsWith("$2b$") && !pwd.startsWith("$2y$")) {
                    user.setPassword(passwordEncoder.encode(pwd));
                    userRepository.save(user);
                    migratedCount++;
                }
            }
            if (migratedCount > 0) {
                System.out.println("Successfully migrated " + migratedCount + " plain text password(s) to secure BCrypt hashes in MySQL.");
            }
        }
    }
}
