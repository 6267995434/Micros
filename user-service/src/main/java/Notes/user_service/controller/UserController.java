package Notes.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Notes.user_service.dto.UserResponse;
import Notes.user_service.exception.ConnectionTimeoutClientException;
import Notes.user_service.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long id) {
		return ResponseEntity.ok(userService.getUserById(id));
	}
	
	@PostMapping("/setPhone")
	public ResponseEntity<Boolean> setPhone(@RequestBody String phone) {
		System.out.println("phone payload " + phone);
		Boolean bs = userService.setPhone(phone);
		
	
		
		return ResponseEntity.ok(bs);
	}
	
	
}
