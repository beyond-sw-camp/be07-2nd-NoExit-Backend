package com.E1i3.NoExit.domain.board.dto;

import com.E1i3.NoExit.domain.board.domain.BoardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardCreateReqDto {
    private String title; // 제목
    private String contents; // 내용
    private BoardType boardType; // 게시판 유형 (자유, 전략)

}