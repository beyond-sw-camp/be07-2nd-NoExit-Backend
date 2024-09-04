package com.E1i3.NoExit.domain.notification.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.E1i3.NoExit.domain.member.domain.Role;
import com.E1i3.NoExit.domain.notification.dto.NotificationResDto;
import com.E1i3.NoExit.domain.notification.repository.NotificationRepository;
import com.E1i3.NoExit.domain.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class SseController implements MessageListener {

	private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
	private final NotificationRepository notificationRepository;
	private Set<String> subscribeList = ConcurrentHashMap.newKeySet();

	// private final Map<String, SseEmitter> ownerEmitters = new
	// ConcurrentHashMap<>();
	// private Set<String> subsbribeOwnerList = ConcurrentHashMap.newKeySet();

	@Qualifier("3")
	private final RedisTemplate<String, Object> sseRedisTemplate;

	private final RedisMessageListenerContainer redisMessageListenerContainer;

	public SseController(@Qualifier("3") RedisTemplate<String, Object> sseRedisTemplate,
			RedisMessageListenerContainer redisMessageListenerContainer,
			NotificationRepository notificationRepository) {
		this.sseRedisTemplate = sseRedisTemplate;
		this.redisMessageListenerContainer = redisMessageListenerContainer;
		this.notificationRepository = notificationRepository;
	}

	public void subscribeChannel(String email) {
		if (!subscribeList.contains(email)) {
			MessageListenerAdapter listenerAdapter = createListerAdapter(this);
			redisMessageListenerContainer.addMessageListener(listenerAdapter, new PatternTopic(email));
			subscribeList.add(email);
		}
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			NotificationResDto dto = objectMapper.readValue(message.getBody(), NotificationResDto.class);
			String email = new String(pattern, StandardCharsets.UTF_8);
			SseEmitter emitter = emitters.get(email);
			System.out.println("emitter : " + emitter);
			if (emitter != null) {
				emitter.send(SseEmitter.event().name(dto.getType().toString()).data(dto));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private MessageListenerAdapter createListerAdapter(SseController sseController) {
		return new MessageListenerAdapter(sseController, "onMessage");
	}

	@GetMapping("/subscribe")
	public SseEmitter subcribe() {
		SseEmitter emitter = new SseEmitter(14400 * 60 * 1000L);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

		emitters.put(email, emitter);
		emitter.onCompletion(() -> emitters.remove(email));
		emitter.onTimeout(() -> emitters.remove(email));

		subscribeChannel(email);
		return emitter;
	}

	public void publishMessage(NotificationResDto dto, String email) {
		notificationRepository.save(dto);
		sseRedisTemplate.convertAndSend(email, dto);
	}
}
