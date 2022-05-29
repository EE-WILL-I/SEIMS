package ru.seims.mailservice;

public class MessageTemplateBean {
    private int id;
    private String subject;
    private String content;
    private String signature;

    public MessageTemplateBean(int id, String subject, String content, String signature) {
        this.id = id;
        this.content = content;
        this.signature = signature;
        this.subject = subject;
    }

    public MessageTemplateBean() {
        this(-1, "", "", "");
    }

    public void setId(int id) { this.id = id; }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getId() { return id; }

    public String getContent() {
        return content;
    }

    public String getSignature() {
        return signature;
    }

    public String getSubject() {
        return subject;
    }
}
