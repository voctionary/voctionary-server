package vsb.fei.voctionary.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invalid_jwt")
@Data
@NoArgsConstructor
public class InvalidJwt{
	
	@Id
	@Column(name = "token", nullable = false, updatable = false)
	private String token;
	
	@Column(name = "created_at", insertable = false, updatable = false, nullable = false,
			columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Instant createdAt;
	
	@Column(name = "expires_at", updatable = false, nullable = false)
	private Instant expiresAt;

	public InvalidJwt(String token, Instant expiresAt) {
		this.token = token;
		this.expiresAt = expiresAt;
	}

}
