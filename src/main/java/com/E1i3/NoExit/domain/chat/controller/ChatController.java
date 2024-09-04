package com.E1i3.NoExit.domain.chat.controller;


import com.E1i3.NoExit.domain.chat.domain.ChatMessageEntity;
import com.E1i3.NoExit.domain.chat.domain.ChatRoom;
import com.E1i3.NoExit.domain.chat.dto.ChatMessage;
import com.E1i3.NoExit.domain.chat.repository.ChatRoomRepository;
import com.E1i3.NoExit.domain.chat.service.ChatService;
import com.E1i3.NoExit.domain.chat.service.RedisMessagePublisher;
//import com.E1i3.NoExit.domain.chat.service.RedisStreamService;
import com.E1i3.NoExit.domain.member.domain.Member;
import com.E1i3.NoExit.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final MemberRepository memberRepository;
//    private final RedisMessagePublisher redisMessagePublisher;
    private final ChatRoomRepository chatRoomRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, StompHeaderAccessor accessor) {
        try {
            // STOMP 헤더에서 사용자 정보 가져오기
            String senderEmail = accessor.getUser().getName();

            // 사용자 정보 조회
            Member member = memberRepository.findByEmail(senderEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + senderEmail));

            // 사용자 이름 및 프로필 이미지 설정
            chatMessage.setSender(senderEmail);
            chatMessage.setSenderName(member.getNickname());
            chatMessage.setSenderProfileImage(member.getProfileImage());

            // 메시지 처리 및 저장
            chatService.handleMessage(chatMessage.getRoomId(), chatMessage.getSender(), chatMessage.getSenderName(), chatMessage.getSenderProfileImage(), chatMessage.getContent());
            // WebSocket을 통해 클라이언트로 메시지 전송
            String destination = "/topic/room/" + chatMessage.getRoomId();
            messagingTemplate.convertAndSend(destination, chatMessage);

            System.out.println("Message sent: " + chatMessage.getContent());

        } catch (Exception e) {
            System.err.println("Error in sendMessage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/chat.join")
    public void joinRoom(@Payload ChatMessage chatMessage, StompHeaderAccessor accessor) {

        try {
            String senderEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println(senderEmail);
            ChatRoom chatRoom = chatRoomRepository.findById(Long.parseLong(chatMessage.getRoomId()))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid room ID"));

            // STOMP 헤더에서 사용자 정보 가져오기

            senderEmail = accessor.getUser().getName();

            // senderEmail 설정
            chatMessage.setSender(senderEmail);

            chatMessage.setContent(senderEmail + " 님이 방에 참가하셨습니다!");

            // 클라이언트로 메시지 전송
            String destination = "/topic/room/" + chatMessage.getRoomId();
            messagingTemplate.convertAndSend(destination, chatMessage);

            System.out.println("User joined room: " + chatMessage.getRoomId());

        } catch (Exception e) {
            System.err.println("Error in joinRoom: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /* 이전 채팅 내역 보기 */
    @GetMapping("/chat/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageEntity>> getMessages(@PathVariable String roomId) {
        List<ChatMessageEntity> messages = chatService.getMessagesForRoom(Long.parseLong(roomId));
        return ResponseEntity.ok(messages);
    }
}
