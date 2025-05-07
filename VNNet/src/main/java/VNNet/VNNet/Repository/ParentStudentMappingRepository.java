package VNNet.VNNet.Repository;

import VNNet.VNNet.Model.ParentStudentMapping;
import VNNet.VNNet.Model.ParentStudentMappingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParentStudentMappingRepository extends JpaRepository<ParentStudentMapping, ParentStudentMappingId> {
    List<ParentStudentMapping> findByUserId(Long userId);
    boolean existsByUserIdAndStudentId(Long userId, Long studentId);
}
