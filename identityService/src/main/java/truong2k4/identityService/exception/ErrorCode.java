package truong2k4.identityService.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
	USER_EXIST(401, "User is existed", HttpStatus.BAD_REQUEST),
	UNAUTHENTICATED(401, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    USER_NOT_EXIST(404, "User is not existed", HttpStatus.NOT_FOUND);
	private ErrorCode(int code, String message, HttpStatus httpStatusCode) {
		this.code = code;
		this.message = message;
		this.httpStatusCode = httpStatusCode;
	}

	private int code;
	private String message;
	private HttpStatus httpStatusCode;
}
