package VNNet.VNNet.Controller;

import VNNet.VNNet.Model.Grade;
import VNNet.VNNet.Model.Student;
import VNNet.VNNet.Model.User;
import VNNet.VNNet.Repository.GradeRepository;
import VNNet.VNNet.Repository.StudentRepository;
import VNNet.VNNet.Repository.UserRepository;
import VNNet.VNNet.Request.GradeRequest;
import VNNet.VNNet.Request.UpdateGradeRequest;
import VNNet.VNNet.Response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GradeController {
    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/parent/grade/{studentId}")
    public ResponseEntity<List<Grade>> getGradesByStudentId(@PathVariable Long studentId) {
        List<Grade> grades = gradeRepository.findByStudent_StudentId(studentId);
        return ResponseEntity.ok(grades);
    }

    @PostMapping("/teacher/add/grade")
    public ResponseEntity<ApiResponse<Grade>> addGrade(@RequestBody GradeRequest gradeRequest) {
        Student student = studentRepository.findById(gradeRequest.getStudentId())
                .orElse(null);
        if (student == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Student not found", null));
        }

        User user = userRepository.findById(gradeRequest.getUserId())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "User not found", null));
        }

        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setUser(user);
        grade.setSubject(gradeRequest.getSubject());
        grade.setScore(gradeRequest.getScore());
        grade.setTerm(gradeRequest.getTerm());

        gradeRepository.save(grade);
        return ResponseEntity.ok().body(new ApiResponse<>(true, "Grade added successfully", grade));
    }

    @GetMapping("/teacher/grade/{studentId}")
    public ResponseEntity<List<Grade>> getGradeByTeacher(@PathVariable Long studentId) {
        try {
            if (!studentRepository.existsById(studentId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<Grade> grades = gradeRepository.findByStudent_StudentId(studentId);
            return ResponseEntity.ok(grades);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/teacher/edit/grade/{studentId}/{gradeId}")
    public ResponseEntity<ApiResponse<Grade>> updateGrade(
            @PathVariable Long studentId,
            @PathVariable Long gradeId,
            @RequestBody UpdateGradeRequest updateGradeRequest) {
        try {
            if (!studentRepository.existsById(studentId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "student not found", null));
            }
            Grade grade = gradeRepository.findById(gradeId)
                    .orElse(null);

            if (grade == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "grade not found", null));
            }
            if (!grade.getStudent().getStudentId().equals(studentId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false, "Grade does not belong to this student", null));
            }
            grade.setSubject(updateGradeRequest.getSubject());
            grade.setScore(updateGradeRequest.getScore());
            grade.setTerm(updateGradeRequest.getTerm());

            Grade updateGrade = gradeRepository.save(grade);
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "Grade updated Successfully", updateGrade));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error" + e.getMessage(), null));
        }
    }

    @DeleteMapping("/teacher/delete/grade/{studentId}/{gradeId}")
    public ResponseEntity<ApiResponse<Grade>> deleteGrade(
            @PathVariable Long studentId,
            @PathVariable Long gradeId) {
        try {
            if (!studentRepository.existsById(studentId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "student not found", null));
            }
            Grade grade = gradeRepository.findById(gradeId)
                    .orElse(null);
            if (grade == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "grade not found", null));
            }
            if (!grade.getStudent().getStudentId().equals(studentId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false, "Grade does not belong to this student", null));
            }
            gradeRepository.delete(grade);
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(true, "Grade deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Eror" + e.getMessage(), null));
        }
    }
}
