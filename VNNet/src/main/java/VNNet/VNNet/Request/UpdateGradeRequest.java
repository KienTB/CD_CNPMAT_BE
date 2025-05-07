package VNNet.VNNet.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGradeRequest {
    private String subject;
    private float score;
    private String term;
}
