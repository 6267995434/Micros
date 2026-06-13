package Notes.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Notes.user_service.client.PaymentClient;
import Notes.user_service.dto.PaymentResponse;
import Notes.user_service.dto.UserResponse;
import Notes.user_service.exception.ConnectionTimeoutClientException;
import feign.FeignException;

@Service
public class UserService {

	@Autowired
	PaymentClient paymentClient;
//	 Expected response when payment-service is DOWN
//
//	  { "id": 1, "name": "Akshay", "email": "aks@gmail.com", "paymentStatus": "UNKNOWN" }
//
//	  (Note: 200 OK, not 500.)
	public UserResponse getUserById(Long id) {
		String mail = "aks@gmail.com";
		String name = "Akshay";
		
		PaymentResponse payment = paymentClient.getPaymentStatus(String.valueOf(id));

		return new UserResponse(id, name, payment.getStatus(),mail);
		
	}
	
	 public boolean setPhone(String phone) {
	        try {
	            boolean set = paymentClient.setPhoneNumber(phone);
	            return set;
	        
	        }  catch (Exception ex) {
	            // Optional: catch other exceptions
	            throw new ConnectionTimeoutClientException("Unexpected error while calling payment service");
	        }
	    }
}
