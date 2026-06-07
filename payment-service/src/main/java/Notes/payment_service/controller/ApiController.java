package Notes.payment_service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class ApiController {
//	GET /payments/{userId}/status
	@GetMapping("{userId}/status")
	
	public ResponseEntity<Map<String,Object>> payStatus(@PathVariable("userId")String userId){
		Map<String, Object> response = new HashMap<>();
		
		String status = "SUCCESS"; // replace with actual service call
		
		
        response.put("userId", userId);
        response.put("status", status);
        response.put("timestamp", System.currentTimeMillis());
        
		return ResponseEntity.ok(response);
	}
}
