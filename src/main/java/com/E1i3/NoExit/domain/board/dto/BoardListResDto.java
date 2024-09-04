package com.E1i3.NoExit.domain.board.dto;

import com.E1i3.NoExit.domain.board.domain.BoardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoardListResDto {
    private Long id;
    private String writer; // 작성자
    private String title; //  제목
    private int boardHits; // 조회수
    private int likes; // 좋아요
    private int comments; // 댓글수
    private BoardType boardType; // 게시판 유형 (자유, 전략)
    private boolean img;
    private LocalDate createdDate;

}