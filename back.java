@Document(collection = "students")
public class Student {
    @Id
    private String id;
    private String rollNumber;
    private String name;
    private String course;
    private int numMajorSubjects;
    private int numMinorSubjects;
    private List<String> optionalSubjects;
    private List<String> workingDays;
    private int classDuration;
    private int lunchBreak;
    private Map<String, DaySchedule> timetable; // per-day timings
}
public class DaySchedule {
    private String startTime; // "09:00"
    private String endTime;   // "16:00"
}
@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    Student findByRollNumber(String rollNumber);
}
@Service
public class TimetableGenerator {

    public Map<String, List<String>> generateTimetable(Student student) {
        Map<String, List<String>> timetable = new HashMap<>();

        int classDuration = student.getClassDuration();
        int lunchBreak = student.getLunchBreak();

        for (String day : student.getWorkingDays()) {
            List<String> dailySchedule = new ArrayList<>();

            // Convert start-end times
            String startTime = student.getTimetable().get(day).getStartTime();
            String endTime = student.getTimetable().get(day).getEndTime();

            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);

            List<String> subjects = new ArrayList<>();
            subjects.addAll(Collections.nCopies(student.getNumMajorSubjects(), "Major"));
            subjects.addAll(Collections.nCopies(student.getNumMinorSubjects(), "Minor"));
            subjects.addAll(student.getOptionalSubjects());

            int i = 0;
            while (start.plusMinutes(classDuration).isBefore(end)) {
                if (i == subjects.size()) i = 0; // repeat subjects if fewer than slots

                String slot = start + " - " + start.plusMinutes(classDuration) + " : " + subjects.get(i);
                dailySchedule.add(slot);

                start = start.plusMinutes(classDuration);

                // Lunch break check
                if (start.equals(LocalTime.NOON)) {
                    start = start.plusMinutes(lunchBreak);
                }
                i++;
            }
            timetable.put(day, dailySchedule);
        }
        return timetable;
    }
}
@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private TimetableGenerator generator;

    @GetMapping("/{rollNumber}")
    public Map<String, List<String>> getTimetable(@PathVariable String rollNumber) {
        Student student = studentRepo.findByRollNumber(rollNumber);
        return generator.generateTimetable(student);
    }
}