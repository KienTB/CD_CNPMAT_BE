package VNNet.VNNet.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateScheduleRequest {
    private String activity;
    private String scheduleDate;
    private String status;
}
