package VNNet.VNNet.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Entity
@Table(name = "parent_student_mapping")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@IdClass(ParentStudentMappingId.class)
public class ParentStudentMapping {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "created_at")
    private Timestamp createdAt;
}
