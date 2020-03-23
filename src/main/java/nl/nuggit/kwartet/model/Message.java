package nl.nuggit.kwartet.model;

public class Message {

    private Type type;
    private String content;

    public Message(String content) {
        this(Type.MESSAGE, content);
    }

    public Message(Type type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        MESSAGE,
        START,
        YOUR_TURN
    }
}