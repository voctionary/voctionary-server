package vsb.fei.voctionary.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vsb.fei.voctionary.model.enums.UserTokenType;

@Entity
@Table(name = "user_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserToken {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	@Column(name = "token", nullable = false)
	private String token;
	
	@Column(name = "type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
	private UserTokenType type;
	
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;
	
	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;
	
	@Column(name = "confirmed_at")
	private Instant confirmedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;
	
	public UserToken(String token, UserTokenType type, Instant createdAt, Instant expiresAt, User user) {
		this.token = token;
		this.type = type;
		this.createdAt = createdAt;
		this.expiresAt = expiresAt;
		this.user = user;
	}
	
}
