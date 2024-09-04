package com.E1i3.NoExit.domain.notification.dto;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.E1i3.NoExit.domain.common.domain.BaseTimeEntity;
import com.E1i3.NoExit.domain.common.domain.DelYN;
import com.E1i3.NoExit.domain.notification.domain.NotificationType;
import com.E1i3.NoExit.domain.reservation.domain.ApprovalStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@JsonIgnoreProperties(ignoreUnknown =true)
public class NotificationResDto extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String sender_email;	// 전송
	private String email;	// 알림 받는이

	@Enumerated(EnumType.STRING)
	private NotificationType type;
	private Long notification_id;
	private String message;

	@Enumerated(EnumType.STRING)
	private ApprovalStatus approvalStatus;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private DelYN delYn = DelYN.N;

	public void updateDelYN() {
		this.delYn = DelYN.Y;
	}
}

