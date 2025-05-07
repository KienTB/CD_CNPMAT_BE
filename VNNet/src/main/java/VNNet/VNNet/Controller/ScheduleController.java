package VNNet.VNNet.Controller;

import VNNet.VNNet.Model.Grade;
import VNNet.VNNet.Model.Schedule;
import VNNet.VNNet.Model.Student;
import VNNet.VNNet.Model.User;
import VNNet.VNNet.Repository.ScheduleRepository;
import VNNet.VNNet.Repository.StudentRepository;
import VNNet.VNNet.Repository.UserRepository;
import VNNet.VNNet.Request.ScheduleRequest;
import VNNet.VNNet.Request.UpdateScheduleRequest;
import VNNet.VNNet.Response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ScheduleController {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/parent/schedule/{studentId}")
    public ResponseEntity<List<Schedule>> getSchedulesByStudentId(@PathVariable Long studentId) {
        List<Schedule> schedules = scheduleRepository.findByStudent_StudentId(studentId);
        return ResponseEntity.ok(schedules);
    }

    @PostMapping("/teacher/add/schedule")
    public ResponseEntity<ApiResponse<Schedule>> addSchedule(@RequestBody ScheduleRequest scheduleRequest) {
        Student student = studentRepository.findById(scheduleRequest.getStudentId())
                .orElse(null);
        if (student == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Student not found", null));
        }

        User user = userRepository.findById(scheduleRequest.getUserId())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "User not found", null));
        }

        Schedule schedule = new Schedule();
        schedule.setStudent(student);
        schedule.setUser(user);
        schedule.setActivity(scheduleRequest.getActivity());
        schedule.setScheduleDate(scheduleRequest.getScheduleDate());
        schedule.setStatus(scheduleRequest.getStatus());

        scheduleRepository.save(schedule);
        return ResponseEntity.ok().body(new ApiResponse<>(true, "Schedule added successfully", schedule));
    }

    @GetMapping("/teacher/schedule/{studentId}")
    public ResponseEntity<List<Schedule>> getScheduleByTeacher(@PathVariable Long studentId){
        try{
            if(!studentRepository.existsById(studentId)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<Schedule> schedules = scheduleRepository.findByStudent_StudentId(studentId);
            return ResponseEntity.ok(schedules);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/teacher/edit/schedule/{studentId}/{scheduleId}")
    public ResponseEntity<ApiResponse<Schedule>> updateSchedule(
            @PathVariable Long studentId,
            @PathVariable Long scheduleId,
            @RequestBody UpdateScheduleRequest updateScheduleRequest){
        try {
            if(!studentRepository.existsById(studentId)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "student not found", null));
            }
            Schedule schedule = scheduleRepository.findById(scheduleId)
                    .orElse(null);
            if(schedule == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "schedule not found", null));
            }
            if(!schedule.getStudent().getStudentId().equals(studentId)){
                return  ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false, "Error", null));
            }
            schedule.setActivity(updateScheduleRequest.getActivity());
            schedule.setScheduleDate(LocalDate.parse(updateScheduleRequest.getScheduleDate()));
            schedule.setStatus(updateScheduleRequest.getStatus());

            Schedule updateSchedule = scheduleRepository.save(schedule);
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "schedule updated successfully", updateSchedule));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error" + e.getMessage(), null));
        }
    }

    @DeleteMapping("/teacher/delete/schedule/{studentId}/{scheduleId}")
    public ResponseEntity<ApiResponse<Grade>> deleteSchedule(
            @PathVariable Long studentId,
            @PathVariable Long scheduleId){
        if(!studentRepository.existsById(studentId)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "student not found", null));
        }
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElse(null);
        if(schedule == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "schedule not found", null));
        }
        if(!schedule.getStudent().getStudentId().equals(studentId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Error", null));
        }
        scheduleRepository.delete(schedule);
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, "Schedule deleted successfully", null));
    }

}
