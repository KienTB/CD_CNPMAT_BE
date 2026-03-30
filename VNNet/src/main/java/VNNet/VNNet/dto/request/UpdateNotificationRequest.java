package VNNet.VNNet.dto.request;

public class UpdateNotificationRequest {
    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UpdateNotificationRequest(String title, String content, Long userId) {
        this.title = title;
        this.content = content;
    }
}
