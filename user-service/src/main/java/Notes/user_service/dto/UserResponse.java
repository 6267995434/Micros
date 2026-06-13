package Notes.user_service.dto;

public class UserResponse {

	private Long id;
	private String name;
	private String paymentStatus;
	private String email;

	

	public UserResponse(Long id, String name, String paymentStatus, String email) {
		super();
		this.id = id;
		this.name = name;
		this.paymentStatus = paymentStatus;
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
