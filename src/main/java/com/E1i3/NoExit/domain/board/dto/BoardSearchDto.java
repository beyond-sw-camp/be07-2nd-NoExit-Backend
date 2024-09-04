package com.E1i3.NoExit.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardSearchDto {
    private String searchBoardType;
    private String searchContents;
    private String searchTitle;
}