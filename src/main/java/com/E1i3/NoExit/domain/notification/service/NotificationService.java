package com.E1i3.NoExit.domain.notification.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityNotFoundException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.E1i3.NoExit.domain.board.domain.Board;
import com.E1i3.NoExit.domain.comment.domain.Comment;
import com.E1i3.NoExit.domain.comment.dto.CommentCreateReqDto;
import com.E1i3.NoExit.domain.common.domain.DelYN;
import com.E1i3.NoExit.domain.member.domain.Member;
import com.E1i3.NoExit.domain.member.domain.Role;
import com.E1i3.NoExit.domain.member.repository.MemberRepository;
import com.E1i3.NoExit.domain.member.service.MemberService;
import com.E1i3.NoExit.domain.notification.controller.NotificationController;

import com.E1i3.NoExit.domain.notification.domain.NotificationType;
import com.E1i3.NoExit.domain.notification.dto.NotificationResDto;
import com.E1i3.NoExit.domain.notification.repository.NotificationRepository;
import com.E1i3.NoExit.domain.owner.service.OwnerService;
import com.E1i3.NoExit.domain.reservation.dto.ReservationSaveDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationService {
	private final NotificationRepository notificationRepository;

	@Autowired
	public NotificationService(NotificationRepository notificationRepository) {
		this.notificationRepository = notificationRepository;
	}

	public List<NotificationResDto> getNotificationsByEmail() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return notificationRepository.findByEmail(email);
	}

	public void updateDelYN(Long id) {
		NotificationResDto notificationResDto = notificationRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 알림입니다."));
		notificationResDto.updateDelYN();
		notificationRepository.save(notificationResDto);
		System.out.println(notificationResDto.getDelYn());
	}
}
