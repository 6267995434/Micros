package Notes.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import Notes.user_service.dto.PaymentResponse;

@FeignClient(name = "payment-service", url = "${payment-service.url}", fallback = PaymentClientFallback.class)
public interface PaymentClient {

	@GetMapping("/payments/{userId}/status")
	PaymentResponse getPaymentStatus(@PathVariable("userId") String userId);
	
	@PostMapping("/payments/phone")
	Boolean setPhoneNumber(@RequestBody String phone);
}
