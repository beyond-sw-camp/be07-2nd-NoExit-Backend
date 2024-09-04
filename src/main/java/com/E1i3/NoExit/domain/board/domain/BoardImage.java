//package com.E1i3.NoExit.domain.board.domain;
//import com.E1i3.NoExit.domain.board.dto.BoardImageDto;
//import com.E1i3.NoExit.domain.board.dto.BoardListResDto;
//import lombok.*;
//
//import javax.persistence.*;
//
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//@Getter
//public class BoardImage {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "board_id")
//    private Board board;
//
//    private String imageUrl;
//
//    public BoardImageDto fromEntity(){
//        BoardImageDto dto = BoardImageDto.builder()
//                .imageUrl(this.imageUrl)
//                .build();
//        return dto;
//    }
//}