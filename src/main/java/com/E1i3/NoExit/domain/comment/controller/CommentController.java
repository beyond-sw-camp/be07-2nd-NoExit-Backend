package com.E1i3.NoExit.domain.comment.controller;

import com.E1i3.NoExit.domain.comment.domain.Comment;
import com.E1i3.NoExit.domain.comment.dto.CommentCreateReqDto;
import com.E1i3.NoExit.domain.comment.dto.CommentListResDto;
import com.E1i3.NoExit.domain.comment.dto.CommentUpdateReqDto;
import com.E1i3.NoExit.domain.comment.service.CommentService;
import com.E1i3.NoExit.domain.common.dto.CommonErrorDto;
import com.E1i3.NoExit.domain.common.dto.CommonResDto;
import com.E1i3.NoExit.domain.notification.service.NotificationService;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags="댓글 서비스")
public class CommentController {

    private final CommentService commentService;
    private final NotificationService notificationService;

    @Autowired
    public CommentController(CommentService commentService, NotificationService notificationService) {
        this.commentService = commentService;
		this.notificationService = notificationService;
	}

    // url은 임의로 대충 붙임

    @Operation(summary= "댓글 작성")
    @PostMapping("/comment/create") // 댓글 생성
    public ResponseEntity<Object> commentCreate(@RequestBody CommentCreateReqDto dto) {
        try {
            Comment comment = commentService.commentCreate(dto);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "comment is successfully created", comment.getId());
            return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }





    @Operation(summary= "댓글 조회")
    @GetMapping("/comment/list/{id}") // 댓글 조회
    public ResponseEntity<Object> commentRead(@PathVariable Long id,
            @PageableDefault(size=10,sort = "createdTime", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CommentListResDto> dtos = commentService.commentList(id, pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "list is successfully found", dtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }




    @Operation(summary= "댓글 조회")
    @GetMapping("/comment/{id}") // 댓글 조회
    public ResponseEntity<Object> commentReadOnlyOne(@PathVariable Long id) {
        CommentListResDto dto = commentService.commentOne(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "comment is successfully found", dto);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }







    @Operation(summary= "댓글 수정")
    @PatchMapping("/comment/update/{id}") // 댓글 수정
    public ResponseEntity<Object> commentUpdate(@PathVariable Long id, @RequestBody CommentUpdateReqDto dto) {
        try {
            commentService.commentUpdate(id, dto);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "comment is successfully updated", null);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }





    @Operation(summary= "댓글 삭제")
    @PatchMapping("/comment/delete/{id}") // 댓글 삭제
    public ResponseEntity<Object> commentDelete(@PathVariable Long id) {
        try {
            commentService.commentDelete(id);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "comment is successfully deleted", null);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary= "댓글 좋아요")
    @PatchMapping("/comment/like/{id}")
    public ResponseEntity<Object> commentLike(@PathVariable Long id) {
        try {
            CommonResDto commonResDto;
            boolean value = false;
            if(commentService.commentUpdateLikes(id)) {
                commonResDto = new CommonResDto(HttpStatus.OK, "you liked this comment", !value);
                return new ResponseEntity<>(commonResDto, HttpStatus.OK);
            } else {
                commonResDto = new CommonResDto(HttpStatus.OK, "you un-liked this comment", value);
                return new ResponseEntity<>(commonResDto, HttpStatus.OK);
            }
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary= "댓글 싫어요")
    @PatchMapping("/comment/dislike/{id}")
    public ResponseEntity<Object> commentDislike(@PathVariable Long id) {
        try {
            CommonResDto commonResDto;
            boolean value = false;
            if(commentService.commentUpdateDislikes(id)) {
                commonResDto = new CommonResDto(HttpStatus.OK, "you disliked this comment", !value);
                return new ResponseEntity<>(commonResDto, HttpStatus.OK);
            } else {
                commonResDto = new CommonResDto(HttpStatus.OK, "you un-disliked this comment", value);
                return new ResponseEntity<>(commonResDto, HttpStatus.OK);
            }
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }
}
