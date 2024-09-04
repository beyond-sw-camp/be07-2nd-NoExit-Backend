package com.E1i3.NoExit.domain.board.domain;
import com.E1i3.NoExit.domain.board.dto.BoardDetailResDto;
import com.E1i3.NoExit.domain.board.dto.BoardListResDto;
import com.E1i3.NoExit.domain.board.dto.BoardUpdateReqDto;
import com.E1i3.NoExit.domain.boardimage.domain.BoardImage;
import com.E1i3.NoExit.domain.boardimage.dto.BoardImageDto;
import com.E1i3.NoExit.domain.comment.domain.Comment;
import com.E1i3.NoExit.domain.comment.dto.CommentListResDto;
import com.E1i3.NoExit.domain.common.domain.BaseTimeEntity;
import com.E1i3.NoExit.domain.common.domain.DelYN;
import com.E1i3.NoExit.domain.member.domain.Member;
import lombok.*;
import javax.persistence.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 아이디

    @ManyToOne //추가 7-25
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String title; //  제목

    private String contents; // 내용

    private int boardHits; // 조회수

    private int likes; // 좋아요 수
    private int dislikes; // 싫어요 수

    @OneToMany(mappedBy = "board", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<BoardImage> imgs = new ArrayList<>(); // 이미지

    private int notificationState; // 알림 상태

    @Enumerated(EnumType.STRING)
    private BoardType boardType; // 게시판 유형 (자유, 공략)

    @OneToMany(mappedBy = "board", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>(); // 게시글에 달린 댓글들

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DelYN delYN = DelYN.N; // 삭제여부

    public BoardListResDto fromEntity(){
        int commentLength = 0;
        for(Comment c : this.comments) {
            if(c.getDelYN().equals(DelYN.N)) { commentLength++; }
        }

        if (this.imgs != null && !this.imgs.isEmpty()) {
            return BoardListResDto.builder()
                    .id(this.id)
                    .writer(this.member.getNickname())
                    .title(this.title)
                    .boardHits(this.boardHits)
                    .likes(this.likes)
                    .comments(commentLength)
                    .boardType(this.boardType)
                    .img(true)
                    .createdDate(this.getCreatedTime().toLocalDate())
                    .build();
        }else{
            return BoardListResDto.builder()
                    .id(this.id)
                    .writer(this.member.getNickname())
                    .title(this.title)
                    .boardHits(this.boardHits)
                    .likes(this.likes)
                    .comments(commentLength)
                    .boardType(this.boardType)
                    .img(false)
                    .createdDate(this.getCreatedTime().toLocalDate())
                    .build();
        }
    }



    public BoardDetailResDto detailFromEntity(){

        List<Comment> comments = this.getComments();
        List<CommentListResDto> cDtos = new ArrayList<>();
        for(Comment c : comments) {
            if(c.getDelYN().equals(DelYN.Y)) {
                continue;
            }
            cDtos.add(c.fromEntity());
        }

        List<BoardImage> images = this.getImgs();
        List<BoardImageDto> iDtos = new ArrayList<>();
        for(BoardImage i : images) {
            if(i.getDelYN().equals(DelYN.N)) {
                iDtos.add(i.fromEntity());
            }
        }

        String createdTime
                = this.getCreatedTime().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        );


        BoardDetailResDto boardDetailResDto = BoardDetailResDto.builder()
                .id(this.id)
                .writer(this.member.getNickname()) //
                .title(this.title)
                .contents(this.contents)
                .boardHits(this.boardHits)
                .likes(this.likes)
                .dislikes(this.dislikes)
                .boardType(this.boardType)
                .comments(cDtos)
                .images(iDtos)
                .createdTime(createdTime)
                .build();

        return boardDetailResDto;
    }

    public void updateEntity(BoardUpdateReqDto dto) {
        this.title = dto.getTitle();
        this.contents = dto.getContents();
        this.boardType = dto.getBoardType();
        for(int i=0; i<this.getImgs().size(); i++) {
            for(int j=0; j<dto.getRemoveImgs().size(); j++) {
                if(this.getImgs().get(i).equals(dto.getRemoveImgs().get(j))) {
                    this.getImgs().get(i).deleteEntity();
                }
            }
        }
    }

    public void deleteEntity() {
        this.delYN = DelYN.Y;
    }

    public void updateBoardHits() {
        this.boardHits++;
    }

    public void updateLikes(boolean like) {
        if(like) {
            this.likes++;
        }else{
            this.likes--;
        }
    }

    public void updateDislikes(boolean dislike) {
        if(dislike) {
            this.dislikes++;
        }else{
            this.dislikes--;
        }
    }

}