package app.workspace.model;

import java.util.List;

public class WebhookEvent {

    private String annotationId;
    private String annotationPayload;
    private String annotationType;
    private String challenge;
    private String content;
    private String contentType;
    private List<String> memberIds;
    private String messageId;
    private String spaceId;
    private String spaceName;
    private String time;
    private String type;
    private String userId;
    private String userName;

    public String getAnnotationId() {
        return annotationId;
    }

    public void setAnnotationId(String annotationId) {
        this.annotationId = annotationId;
    }

    public String getAnnotationPayload() {
        return annotationPayload;
    }

    public void setAnnotationPayload(String annotationPayload) {
        this.annotationPayload = annotationPayload;
    }

    public String getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(String annotationType) {
        this.annotationType = annotationType;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "OutboundWebhookEvent{" +
                "annotationId='" + annotationId + '\'' +
                ", annotationPayload='" + annotationPayload + '\'' +
                ", annotationType='" + annotationType + '\'' +
                ", challenge='" + challenge + '\'' +
                ", content='" + content + '\'' +
                ", contentType='" + contentType + '\'' +
                ", memberIds=" + memberIds +
                ", messageId='" + messageId + '\'' +
                ", spaceId='" + spaceId + '\'' +
                ", spaceName='" + spaceName + '\'' +
                ", time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
