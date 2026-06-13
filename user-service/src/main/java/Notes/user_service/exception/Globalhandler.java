package Notes.user_service.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class Globalhandler {
	
	@ExceptionHandler(ConnectionTimeoutClientException.class)
	public ResponseEntity<Map<String,Object>> handleTimeout(ConnectionTimeoutClientException ex){
		
		Map<String,Object> mp = new HashMap<String, Object>();
		
		mp.put("timestamp", LocalDateTime.now());
		mp.put("status", HttpStatus.REQUEST_TIMEOUT.value());
		mp.put("error","Read TimeOut");
		mp.put("msg", ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(mp);
	}
}
