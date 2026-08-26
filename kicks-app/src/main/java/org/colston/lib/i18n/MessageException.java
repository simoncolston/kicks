package org.colston.lib.i18n;

public class MessageException extends Exception {

    private final Message message;

    public MessageException(Class<?> clz, String key, Object... args) {
        this(new Message(clz, key, args));
    }

    public MessageException(Throwable cause, Class<?> clz, String key, Object... args) {
        this(cause, new Message(clz, key, args));
    }

    public MessageException(Message message) {
        this.message = message;
    }

    public MessageException(Throwable cause, Message message) {
        super(cause);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return getLocalizedMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return Messages.get(message);
    }
}
