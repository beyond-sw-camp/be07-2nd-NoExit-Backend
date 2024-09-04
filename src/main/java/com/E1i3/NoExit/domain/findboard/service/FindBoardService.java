package com.E1i3.NoExit.domain.findboard.service;

import com.E1i3.NoExit.domain.attendance.domain.Attendance;
import com.E1i3.NoExit.domain.attendance.repositroy.AttendanceRepository;
import com.E1i3.NoExit.domain.attendance.service.AttendanceService;
import com.E1i3.NoExit.domain.chat.domain.ChatRoom;
import com.E1i3.NoExit.domain.chat.service.ChatRoomService;
import com.E1i3.NoExit.domain.common.domain.DelYN;
import com.E1i3.NoExit.domain.findboard.domain.FindBoard;
import com.E1i3.NoExit.domain.findboard.dto.*;
import com.E1i3.NoExit.domain.findboard.repository.FindBoardRepository;
import com.E1i3.NoExit.domain.member.domain.Member;
import com.E1i3.NoExit.domain.member.repository.MemberRepository;
import com.E1i3.NoExit.domain.notification.controller.SseController;
import com.E1i3.NoExit.domain.notification.domain.NotificationType;
import com.E1i3.NoExit.domain.notification.dto.NotificationResDto;
import com.E1i3.NoExit.domain.notification.repository.NotificationRepository;
import com.E1i3.NoExit.domain.notification.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class FindBoardService {

    private final AttendanceService attendanceService;
    private final FindBoardRepository findBoardRepository;
    private final MemberRepository memberRepository;
    private final AttendanceRepository attendanceRepository;
    private final NotificationRepository notificationRepository;
    private final SseController sseController;
    private final ChatRoomService chatRoomService;

    @Autowired
    public FindBoardService(AttendanceService attendanceService, FindBoardRepository findBoardRepository, MemberRepository memberRepository, AttendanceRepository attendanceRepository, NotificationRepository notificationRepository,
                            SseController sseController, ChatRoomService chatRoomService) {
        this.attendanceService = attendanceService;
        this.findBoardRepository = findBoardRepository;
        this.memberRepository = memberRepository;
        this.attendanceRepository = attendanceRepository;
		this.notificationRepository = notificationRepository;
		this.sseController = sseController;
        this.chatRoomService = chatRoomService;
    }


    public void findBoardCreate(FindBoardSaveReqDto findBoardSaveReqDto) {

        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("회원 가입 후 글 작성이 가능합니다."));

        FindBoard findBoard = findBoardSaveReqDto.toEntity(member);
        findBoardRepository.save(findBoard);

//        incrementParticipantCount(findBoard.getId());

        Attendance attendance = Attendance.builder() // Attendance 엔티티에 참가신청 버튼을 누른 회원의 정보를 id로 추가
                .findBoard(findBoard) // 게시글 id
                .member(member)
                .email(memberEmail)// 참석자 id
                .build();

        attendanceRepository.save(attendance); // 참가자 정보 저장
        findBoard.incrementCurrentCount();
    }

//    @Transactional(readOnly = true)
//    public Page<FindBoardListResDto> findBoardListResDto(Pageable pageable) {
//
//        Page<FindBoard> findBoards = findBoardRepository.findByDelYn(pageable, DelYN.N);
//        return findBoards.map(FindBoard::listFromEntity);
//
//    }


    @Transactional
    public FindBoardResDto update(Long id, FindBoardUpdateReqDto dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        FindBoard findBoard = findBoardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("not found id : " + id));

        if ( !findBoard.getMember().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 게시글만 수정할 수 있습니다.");
        }

        // 삭제된 게시글인지 체크
        if (findBoard.getDelYn() == DelYN.Y) {
            throw new IllegalStateException("삭제된 게시글은 수정할 수 없습니다.");
        }

        findBoard.updateFromDto(dto);
        return findBoard.ResDtoFromEntity();
    }


    @Transactional
    public String delete(Long id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        FindBoard findBoard = findBoardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));
// 아래 예외를 위로 옮기는것 고려
        if ( !findBoard.getMember().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 게시글만 삭제할 수 있습니다.");
        }

        findBoard.markAsDeleted();
        return "게시글 삭제 완료";
    }

    public FindBoardResDto incrementParticipantCount(Long id) {

        FindBoard findBoard = findBoardRepository.findByIdAndDelYn(id, DelYN.N)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new EntityNotFoundException("참가 신청은 로그인 후 가능합니다."));

//         게시글 작성자와 로그인한 사용자가 동일한지 확인
        if (findBoard.getMember().getEmail().equals(memberEmail)) {
            throw new IllegalStateException("자신의 게시글에 참가 신청을 할 수 없습니다.");
        }

        findBoard.incrementCurrentCount();




        Attendance attendance = Attendance.builder() // Attendance 엔티티에 참가신청 버튼을 누른 회원의 정보를 id로 추가
                .findBoard(findBoard) // 게시글 id
                .member(member)
                .email(memberEmail)
                // 참석자 id
                .build();

        attendanceRepository.save(attendance); // 참가자 정보 저장

        if ( findBoard.getCurrentCount() == findBoard.getTotalCapacity()){



            String receiver_email = findBoard.getMember().getEmail();   // 신청한 사람

            // NotificationResDto notificationResDto = NotificationResDto.builder()
            //     .findboard_id(findBoard.getId())
            //     .email(receiver_email)
            //     .type(NotificationType.FULL_COUNT)
            //     .message("참여글의 모집인원이 가득찼습니다.").build();
            // sseController.publishMessage(notificationResDto, receiver_email);

            ChatRoom chatRoom = chatRoomService.createRoom(findBoard.getTitle() , "0000");
            List<Attendance> attendances = attendanceRepository.findByFindBoardId(findBoard.getId());

            // 채팅방에 참여한 모든 사용자에게 알림 전송
            NotificationResDto participantNotification;
            for (Attendance a : attendances) {
                a.setChatRoom(chatRoom);
                attendanceRepository.save(a);

                participantNotification = NotificationResDto.builder()
                    .notification_id(chatRoom.getRoomId())
                    .email(a.getMember().getEmail())
                    .type(NotificationType.CHAT_ROOM_INVITE)
                    .message("참여한 채팅방이 생성되었습니다.\n 채팅방 이름: " + chatRoom.getName())
                    .build();
                sseController.publishMessage(participantNotification, a.getMember().getEmail());
            }
            findBoard.markAsDeleted(); // 참가 인원이 꽉 차면 게시글을 Y로 변경 , 위치 수정 if문 밖에 잇어서 옮겨놨음. 혹시 문제생기면 다시 빼기 8.18
            attendanceService.markAttendancesAsDeleted(findBoard.getId());
        }
//        // 게시글 작성자도 attendance에 추가해서 채팅 목록에 나올 수 있도록
//        attendance = Attendance.builder()
//            .findBoard(findBoard) // 게시글 id
//            .member(member) // 참석자 id
//            .build();
//        attendanceRepository.save(attendance);



        return findBoard.ResDtoFromEntity();
    }

    @Transactional(readOnly = true)
    public List<FindBoardListResDto> getImminentClosingBoards() {
        List<FindBoard> imminentBoards = findBoardRepository.findImminentClosingBoards();

        return imminentBoards.stream()
                .map(FindBoard::listFromEntity)
                .collect(Collectors.toList());
    }
    public Page<FindBoardListResDto> findBoardList(FindBoardSearchDto searchDto, Pageable pageable) {
        Specification<FindBoard> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 제목 검색 조건 추가
            if (searchDto.getTitle() != null && !searchDto.getTitle().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + searchDto.getTitle() + "%"));
            }

            // 내용 검색 조건 추가
            if (searchDto.getContents() != null && !searchDto.getContents().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("contents"), "%" + searchDto.getContents() + "%"));
            }

            // DelYN 조건 추가: 삭제되지 않은 게시글만 조회
            predicates.add(criteriaBuilder.equal(root.get("delYn"), DelYN.N));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<FindBoard> findBoards = findBoardRepository.findAll(specification, pageable);
        return findBoards.map(FindBoard::listFromEntity);
    }

}
