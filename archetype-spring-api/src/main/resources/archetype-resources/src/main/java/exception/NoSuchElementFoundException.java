package ${groupId}.exception;

public class NoSuchElementFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoSuchElementFoundException(String message){
        super(message);
    }

    public NoSuchElementFoundException(String message,Throwable e){
        super(message,e);
    }
}