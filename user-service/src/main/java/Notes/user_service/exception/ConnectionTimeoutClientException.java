package Notes.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
public class ConnectionTimeoutClientException extends RuntimeException {

	public ConnectionTimeoutClientException(String message) {
		super(message);
	}
}