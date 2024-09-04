package com.E1i3.NoExit.domain.board.dto;

import com.E1i3.NoExit.domain.board.domain.BoardType;
import com.E1i3.NoExit.domain.boardimage.dto.BoardImageDto;
import com.E1i3.NoExit.domain.comment.dto.CommentListResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoardDetailResDto {
    private Long id; // 아이디
    private String writer; // 작성자
    private String title; //  제목
    private String contents; // 내용
    private int boardHits; // 조회수
    private int likes; // 좋아요
    private int dislikes; // 싫어요
    private List<CommentListResDto> comments; // 댓글
    private String createdTime; // 작성일
    private List<BoardImageDto> images; // 이미지
    private BoardType boardType; // 게시판 유형 (자유, 전략)
}