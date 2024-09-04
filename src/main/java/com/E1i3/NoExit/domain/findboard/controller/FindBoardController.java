package com.E1i3.NoExit.domain.findboard.controller;

import com.E1i3.NoExit.domain.board.dto.BoardListResDto;
import com.E1i3.NoExit.domain.common.dto.CommonResDto;
import com.E1i3.NoExit.domain.findboard.dto.*;
import com.E1i3.NoExit.domain.findboard.service.FindBoardService;
import com.E1i3.NoExit.domain.member.dto.MemberUpdateDto;
import com.E1i3.NoExit.domain.notification.service.NotificationService;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/findboard")
@Api(tags="Escape with me 서비스")
public class FindBoardController {

    private final FindBoardService findBoardService;
    private final NotificationService notificationService;

    @Autowired
    public FindBoardController(FindBoardService findBoardService, NotificationService notificationService) {
        this.findBoardService = findBoardService;
		this.notificationService = notificationService;
	}

    @Operation(summary= "[일반 사용자] 번개 글 작성 API")
    @PostMapping("/create")
    public ResponseEntity<?> findBoardCreate(@RequestBody FindBoardSaveReqDto findBoardSaveReqDto) {

        findBoardService.findBoardCreate(findBoardSaveReqDto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "등록 완료",findBoardSaveReqDto.getWriter());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    @Operation(summary= "[일반 사용자] 번개 글 게시판 API")
    @GetMapping("/list")
    public ResponseEntity<CommonResDto> getFindBoardList(
            @PageableDefault(size=6, sort = "createdTime", direction = Sort.Direction.DESC) Pageable pageable,
            FindBoardSearchDto searchDto) {

        Page<FindBoardListResDto> findBoardListResDtos = findBoardService.findBoardList(searchDto, pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "조회 성공", findBoardListResDtos);

        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @Operation(summary= "[일반 사용자] 번개 글 수정 API")
    @PutMapping("/update/{id}")
    public ResponseEntity<CommonResDto> updateFindBoard(@PathVariable Long id, @RequestBody FindBoardUpdateReqDto findBoardUpdateReqDto) {
        FindBoardResDto updatedFindBoardResDto = findBoardService.update(id, findBoardUpdateReqDto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "업데이트 성공", updatedFindBoardResDto);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @Operation(summary= "[일반 사용자] 번개 글 삭제 API")
    @PutMapping("/delete/{id}")
    public ResponseEntity<CommonResDto> deleteFindBoard(@PathVariable Long id) {
        String resultMessage = findBoardService.delete(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, resultMessage, id);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @Operation(summary= "[일반 사용자] 번개 글 참석 API")
    @PutMapping("/participate/{id}")
    public ResponseEntity<CommonResDto> incrementParticipantCount(@PathVariable Long id) {
            try {
                FindBoardResDto updatedFindBoardResDto = findBoardService.incrementParticipantCount(id);
                if (updatedFindBoardResDto.getCurrentCount() > updatedFindBoardResDto.getTotalCapacity()) {
                    return new ResponseEntity<>(new CommonResDto(HttpStatus.BAD_REQUEST, "참가자가 이미 가득 찼습니다.", null), HttpStatus.BAD_REQUEST);
                }
                CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "참가자 수 증가 성공", updatedFindBoardResDto);
                return new ResponseEntity<>(commonResDto, HttpStatus.OK);
            } catch (EntityNotFoundException e) {
            CommonResDto commonResDto = new CommonResDto(HttpStatus.NOT_FOUND, "게시글이 존재하지 않거나 삭제된 게시글입니다.", null);
            return new ResponseEntity<>(commonResDto, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary= "[일반 사용자] 마감 임박 게시글 조회 API")
    @GetMapping("/imminent-closing")
    public ResponseEntity<CommonResDto> getImminentClosingBoards() {
        List<FindBoardListResDto> imminentBoards = findBoardService.getImminentClosingBoards();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "마감 임박 게시글 조회 성공", imminentBoards);

        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

//    분리 방식
//    @GetMapping("/search")
//    public ResponseEntity<CommonResDto> searchFindBoard(FindBoardSearchDto searchDto, Pageable pageable) {
//        Page<FindBoardListResDto> searchResults = findBoardService.findBoardList(searchDto, pageable);
//        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "조회 성공", searchResults);
//        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
//    }


}
