package VNNet.VNNet.Service;

import VNNet.VNNet.Model.ParentStudentMapping;
import VNNet.VNNet.Model.Student;
import VNNet.VNNet.Model.User;
import VNNet.VNNet.Repository.ParentStudentMappingRepository;
import VNNet.VNNet.Repository.StudentRepository;
import VNNet.VNNet.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ParentStudentService {
    private static final Logger logger = LoggerFactory.getLogger(ParentStudentService.class);

    @Autowired
    private ParentStudentMappingRepository parentStudentMappingRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Boolean addStudentToParent(Long userId, Long studentId) {
        try {
            if (!userRepository.existsById(userId)) {
                logger.warn("Không tìm thấy phụ huynh với ID: {}", userId);
                return false;
            }

            if (!studentRepository.existsById(studentId)) {
                logger.warn("Không tìm thấy học sinh với ID: {}", studentId);
                return false;
            }

            if (parentStudentMappingRepository.existsByUserIdAndStudentId(userId, studentId)) {
                logger.info("Mối quan hệ đã tồn tại giữa phụ huynh {} và học sinh {}", userId, studentId);
                return false;
            }

            ParentStudentMapping mapping = ParentStudentMapping.builder()
                    .userId(userId)
                    .studentId(studentId)
                    .createdAt(Timestamp.from(Instant.now()))
                    .build();

            parentStudentMappingRepository.save(mapping);
            logger.info("Đã thêm quan hệ giữa phụ huynh {} và học sinh {}", userId, studentId);
            return true;
        } catch (Exception e) {
            logger.error("Lỗi khi thêm học sinh cho phụ huynh", e);
            return false;
        }
    }

    public List<Student> getStudentsByParentId(Long parentId) {
        try {
            // Kiểm tra tồn tại của phụ huynh
            if (!userRepository.existsById(parentId)) {
                logger.warn("Không tìm thấy phụ huynh với ID: {}", parentId);
                return new ArrayList<>();
            }

            // Lấy danh sách mapping
            List<ParentStudentMapping> mappings = parentStudentMappingRepository.findByUserId(parentId);

            // Lấy danh sách học sinh từ mapping
            List<Student> students = new ArrayList<>();
            for (ParentStudentMapping mapping : mappings) {
                Optional<Student> studentOpt = studentRepository.findById(mapping.getStudentId());
                studentOpt.ifPresent(students::add);
            }

            return students;
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách học sinh của phụ huynh", e);
            return new ArrayList<>();
        }
    }
}
