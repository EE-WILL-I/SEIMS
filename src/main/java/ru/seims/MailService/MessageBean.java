package ru.seims.MailService;

public class MessageBean {
    private String title;
    private String content;
    private String[] recipients;

    public MessageBean(String title, String content, String[] recipients) {
        this.title = title;
        this.content = content;
        this.recipients = recipients;
    }

    public MessageBean() { this("", "", null); }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRecipients(String[] recipients) { this.recipients = recipients; }

    public String getTitle() { return title; }

    public String  getContent() { return content; }

    public String[] getRecipients() { return recipients; }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(recipients == null)
            builder.append("No recipients");
        else for(String recipient : recipients)
            builder.append(recipient);
        return "\nTitle: " + title + "\nContent: " + content + "\nRecipients: " + builder;
    }
}
