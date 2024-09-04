package com.E1i3.NoExit.domain.findboard.domain;

//import com.E1i3.NoExit.domain.findboard.dto.FindBoardDetailResDto;
import com.E1i3.NoExit.domain.attendance.domain.Attendance;
import com.E1i3.NoExit.domain.common.domain.BaseTimeEntity;
import com.E1i3.NoExit.domain.common.domain.DelYN;
import com.E1i3.NoExit.domain.findboard.dto.FindBoardListResDto;
import com.E1i3.NoExit.domain.findboard.dto.FindBoardResDto;
import com.E1i3.NoExit.domain.findboard.dto.FindBoardUpdateReqDto;
import com.E1i3.NoExit.domain.member.domain.Member;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FindBoard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    // wirter = nickname 이라면 사용자에게 데이터
    @Column(length = 100, nullable = true)
    private String writer;

    private String selectedStoreName;

    @Column(length = 3000)
    private String contents;

    private LocalDateTime expirationTime;
    private int currentCount;
    private int totalCapacity;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DelYN delYn = DelYN.N;

    @Lob
    @Column(name = "image", nullable = true)
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY) //참조 안 하면 안 나가게.
    @JoinColumn(name = "member_id")
    private Member member; //이게 필요가 없다? 아니지 saveReqDto에만 필요없지.

    //참석자 연관관계 추가
    @OneToMany(mappedBy = "findBoard", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<Attendance> attendances = new ArrayList<>();

    public FindBoardResDto ResDtoFromEntity() {
        return FindBoardResDto.builder()
                .member_id(this.member.getId()) // 작성자 ID 추가
                .id(this.id)
                .writer(this.member.getNickname())
                .title(this.title)
                .contents(this.contents)
                .createdTime(this.getCreatedTime())
                .updateTime(this.getUpdateTime())
                .expirationTime(this.expirationTime)
                .currentCount(this.currentCount)
                .totalCapacity(this.totalCapacity)
                .imagePath(this.member.getProfileImage())
                .build();
    }

//    //생각해보니까 FindBoard에는 리스트만 잇으면 되지않나? 어차피 글 안에 들어가는게 아니잖아
//    //ResDto도 필요없을 것 같은데.. 생각해보길 바람.
    public FindBoardListResDto listFromEntity(){
        return FindBoardListResDto.builder()
                .member_id(this.member.getId()) // 작성자 ID 추가
                .id(this.id)
                .writer(this.member.getNickname())
                .title(this.title)
                .contents(this.contents)
                .createdTime(this.getCreatedTime())
                .expirationTime(this.expirationTime)
                .currentCount(this.currentCount)
                .totalCapacity(this.totalCapacity)
                .imagePath(this.member.getProfileImage())
                .selectedStoreName(this.selectedStoreName)
                .build();
    }

    public void markAsDeleted() {
        this.delYn = DelYN.Y;
    }

    public void incrementCurrentCount() {
        this.currentCount++;
    }

    public void updateFromDto(FindBoardUpdateReqDto dto) {
        this.title = dto.getTitle();
        this.contents = dto.getContents();
        this.expirationTime = dto.getExpirationDate();
        this.totalCapacity = dto.getTotalCapacity();
        this.getUpdateTime(); // 데이터 값 이상한지 확인하기.
    }
}