package com.E1i3.NoExit.domain.board.dto;

import com.E1i3.NoExit.domain.board.domain.BoardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoardDetailResDto {
    private Long id; // 아이디
    private String writer; // 작성자
    private String title; //  제목
    private String content; // 내용
    private String category; // 카테고리
    private int board_hits; // 조회수
    private int likes; // 좋아요
    private int dislikes; // 싫어요
    private LocalDateTime created_at; // 작성시간
    private LocalDateTime update_at; // 수정시간
    private String image_path; // 이미지
    private BoardType board_type; // 게시판 유형 (자유, 전략)
}
