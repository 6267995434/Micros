package Notes.user_service.client;

import org.springframework.stereotype.Component;

import Notes.user_service.dto.PaymentResponse;

@Component
public class PaymentClientFallback implements PaymentClient {
    @Override
    public PaymentResponse getPaymentStatus(String userId) {
        return new PaymentResponse(userId,"UNKNOWN_Fallback");  // safe default
    }
    
    @Override
    public Boolean setPhoneNumber(String phone) {
        return false;
    }
}
