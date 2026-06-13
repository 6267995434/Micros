package Notes.user_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {

	private String userId;
	private String status;
	private Long timestamp;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public PaymentResponse() {}

	public PaymentResponse(String status) {
		super();
		this.status = status;
	}

	public PaymentResponse(String userId, String status) {
		super();
		this.userId = userId;
		this.status = status;
		this.timestamp = System.currentTimeMillis();
	}
	
	
}
