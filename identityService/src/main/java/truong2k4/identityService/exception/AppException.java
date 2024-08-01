package truong2k4.identityService.exception;

public class AppException extends RuntimeException {
	private ErrorCode errorCode;

	public AppException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
