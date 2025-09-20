import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.util.List;

@Data
@Document(collection = "students")
public class Student {
    @Id
    private String id;
    private String rollNumber;
    private String studentName;
    private String courseSubject;
    private int numberOfMajorSubjects;
    private int numberOfMinorSubjects;
    private List<String> optionalSubjects;
    private List<String> workingDays;
}
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
import java.util.Map;

@Data
@Document(collection = "timetable_forms")
public class TimetableForm {
    @Id
    private String id;
    private int durationOfOneClass;
    private int durationOfLunchBreak;
    private Map<String, TimeSlot> timetable;

    @Data
    public static class TimeSlot {
        private LocalTime startTime;
        private LocalTime endTime;
    }
}
import lombok.Data;

import java.time.LocalTime;

@Data
public class TimetableEntry {
    private String day;
    private String subject;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
    // You can add more fields like faculty, type (theory/practical)
}
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
}
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TimetableService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TimetableFormRepository timetableFormRepository;

    public List<TimetableEntry> generateTimetable(String formId) {
        // 1. Fetch data from MongoDB
        TimetableForm form = timetableFormRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Timetable form not found"));

        List<Student> students = studentRepository.findAll();
        
        // Let's assume you have a way to get a list of all subjects, rooms, and faculty
        // For this example, we'll use a simplified set.
        List<String> subjects = new ArrayList<>();
        students.forEach(s -> {
            subjects.add(s.getCourseSubject());
            if (s.getOptionalSubjects() != null) {
                subjects.addAll(s.getOptionalSubjects());
            }
        });

        List<TimetableEntry> timetable = new ArrayList<>();
        
        // 2. The core generation loop
        int subjectIndex = 0;
        for (String day : form.getTimetable().keySet()) {
            TimetableForm.TimeSlot dailySlot = form.getTimetable().get(day);
            LocalTime currentTime = dailySlot.getStartTime();
            
            while (currentTime.isBefore(dailySlot.getEndTime())) {
                if (subjectIndex >= subjects.size()) {
                    break; // No more subjects to schedule
                }

                String currentSubject = subjects.get(subjectIndex);
                LocalTime classEndTime = currentTime.plusMinutes(form.getDurationOfOneClass());
                
                // Simplified constraint check (you'll expand on this)
                boolean isClash = false; 
                // In a real system, you'd check for faculty, room, and student clashes
                
                if (!isClash && classEndTime.isBefore(dailySlot.getEndTime())) {
                    TimetableEntry entry = new TimetableEntry();
                    entry.setDay(day);
                    entry.setSubject(currentSubject);
                    entry.setStartTime(currentTime);
                    entry.setEndTime(classEndTime);
                    entry.setRoom("Room A"); // You need a mechanism to assign rooms
                    timetable.add(entry);
                    
                    currentTime = classEndTime;
                    subjectIndex++;
                } else {
                    // Move to the next day or handle the time slot differently
                    break;
                }
                
                // Add a lunch break if applicable
                if (currentTime.getHour() == 13) { // Example for 1 PM lunch
                    currentTime = currentTime.plusMinutes(form.getDurationOfLunchBreak());
                }
            }
        }

        return timetable;
    }
}
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/timetables")
@CrossOrigin(origins = "http://localhost:3000") // Adjust this to your frontend URL
public class TimetableController {

    @Autowired
    private TimetableService timetableService;

    @GetMapping("/generate/{formId}")
    public List<TimetableEntry> generateTimetable(@PathVariable String formId) {
        return timetableService.generateTimetable(formId);
    }
}