package com.E1i3.NoExit.domain.board.controller;

import com.E1i3.NoExit.domain.board.domain.Board;
import com.E1i3.NoExit.domain.board.dto.*;
import com.E1i3.NoExit.domain.board.service.BoardService;
import com.E1i3.NoExit.domain.common.dto.CommonErrorDto;
import com.E1i3.NoExit.domain.common.dto.CommonResDto;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Api(tags="게시판 서비스")
public class BoardController {

    private final BoardService boardService;
    @Autowired
    public BoardController(BoardService boardService) {

        this.boardService = boardService;
    }


    @Operation(summary= "게시글 작성")
    @PostMapping("/board/create") // 게시글 생성
    public ResponseEntity<Object> boardCreate(@RequestPart(value = "data") BoardCreateReqDto dto,
                                              @RequestPart(value = "file", required = false) List<MultipartFile> imgFiles
    ) {
        try {
            Board board = boardService.boardCreate(dto, imgFiles);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "board is successfully created", board.getId());
            return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }





    @Operation(summary= "게시글 전체 조회")
    @GetMapping("/board/list") // 게시글 전체 조회
    public ResponseEntity<Object> boardRead(BoardSearchDto searchDto,
                                            @PageableDefault(size=20,sort = "createdTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BoardListResDto> dtos =  boardService.boardList(searchDto, pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "list is successfully found", dtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }





    @Operation(summary= "게시글 상세 조회")
    @GetMapping("/board/detail/{id}") // 게시글 상세조회
    public ResponseEntity<?> boardDetailRead(@PathVariable Long id) {
        BoardDetailResDto dto = boardService.boardDetail(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "board is successfully found", dto);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }




    @Operation(summary= "게시글 수정")
    @PatchMapping("/board/update/{id}") // 게시글 수정
    public ResponseEntity<Object> boardUpdate(
            @PathVariable Long id,
            @RequestPart(value = "data", required = false) BoardUpdateReqDto boardUpdateReqDto,
            @RequestPart(value = "file", required = false) List<MultipartFile> imgFiles
    ) {
        try {
            boardService.boardUpdate(id, boardUpdateReqDto, imgFiles);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "board is successfully updated", null);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }



    @Operation(summary= "게시글 삭제")
    @PatchMapping("/board/delete/{id}") // 게시글 삭제
    public ResponseEntity<Object> boardDelete(@PathVariable Long id) {
        try {
            boardService.boardDelete(id);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "board is successfully deleted", null);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary= "게시글 좋아요")
    @PatchMapping("/board/like/{id}")
    public ResponseEntity<Object> boardLike(@PathVariable Long id) {
        try {
            CommonResDto commonResDto;
            boolean value = false;
            if(boardService.boardUpdateLikes(id)) {
                commonResDto = new CommonResDto(HttpStatus.OK, "you liked this board", !value);
                return new ResponseEntity<>(commonResDto, HttpStatus.OK);
            } else {
                commonResDto = new CommonResDto(HttpStatus.OK, "you un-liked this board", value);
                return new ResponseEntity<>(commonResDto, HttpStatus.OK);
            }
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }




    @Operation(summary= "게시글 싫어요")
    @PatchMapping("/board/dislike/{id}")
    public ResponseEntity<Object> boardDislike(@PathVariable Long id) {
        try {
            CommonResDto commonResDto;
            boolean value = false;
            if(boardService.boardUpdateDislikes(id)) {
                commonResDto = new CommonResDto(HttpStatus.OK, "you disliked this board", !value);
                return new ResponseEntity<>(commonResDto, HttpStatus.OK);
            } else {
                commonResDto = new CommonResDto(HttpStatus.OK, "you un-disliked this board", value);
                return new ResponseEntity<>(commonResDto, HttpStatus.OK);
            }
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }

}