package com.E1i3.NoExit.domain.member.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.E1i3.NoExit.domain.attendance.domain.Attendance;
import com.E1i3.NoExit.domain.board.domain.Board;

import com.E1i3.NoExit.domain.chat.domain.ChatRoom;
import com.E1i3.NoExit.domain.common.domain.BaseTimeEntity;
import com.E1i3.NoExit.domain.common.domain.DelYN;
import com.E1i3.NoExit.domain.grade.domain.Grade;
import com.E1i3.NoExit.domain.member.dto.MemberDetResDto;
import com.E1i3.NoExit.domain.member.dto.MemberListResDto;
import com.E1i3.NoExit.domain.member.dto.MemberRankingResDto;
import com.E1i3.NoExit.domain.member.dto.MemberSaveReqDto;
import com.E1i3.NoExit.domain.findboard.domain.FindBoard;
import com.E1i3.NoExit.domain.member.dto.MemberUpdateDto;
import com.E1i3.NoExit.domain.reservation.domain.Reservation;
import com.E1i3.NoExit.domain.review.domain.Review;
import com.E1i3.NoExit.domain.wishlist.domain.WishList;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTimeEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 50, nullable = false)
	private String username;

	@Column(length = 255, nullable = false)
	private String password;

	@Column(length = 100, unique = true)
	private String email;

	@Column(nullable = false)
	private int point;

	private int age;
	@Column(nullable = true)
	private String profileImage;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private Role role = Role.USER;

	@Column(length = 255, nullable = false)
	private String phone_number;

	@Column(length = 100, nullable = false, unique = true)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private DelYN delYN = DelYN.N;


	// 참석자 연관계 추가
	@OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
	private List<Attendance> attendances;

	@OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
	private List<Review> reviews;

	@OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
	private List<Reservation> reservations;

//	@OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
//	private List<Board> boards;

	// Member객체에 Findboard 객체 추가. : 김민성
	@OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
	private List<FindBoard> findBoards;

	@OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
	@Builder.Default
	private List<WishList> wishList = new ArrayList<>();

	// Grade와 연관 관계 추가 : 김민성
	@ManyToOne
	@JoinColumn(name = "grade_id")
	private Grade grade;
	//
	// // 채팅 방 구현을 위한 추가 , 역 직렬화 막기 위한 조건 추가
	// @ManyToMany(mappedBy = "members")
	// @JsonBackReference // Member는 역참조로 직렬화 제외
	// private List<ChatRoom> chatRooms = new ArrayList<>();

	public Member updateMember(MemberUpdateDto dto, String email, String encodedPassword, String imgUrl) {
		this.username = dto.getUsername();
		this.email = email;
		this.password =  encodedPassword;
		this.age = dto.getAge();
		this.phone_number = dto.getPhone_number();
		this.nickname = dto.getNickname();
		this.profileImage = imgUrl;
		return this;
	}

	public Member updateDelYN() {
		this.delYN = DelYN.Y;
		return this;
	}

	public void updateImgPath(String imgPath) {
		this.profileImage = imgPath;
	}

	public MemberListResDto fromEntity(){
		return MemberListResDto.builder()
			.email(this.email)
			.id(this.id)
			.nickname(this.nickname)
			.username(this.username)
			.build();
	}

	// 사용자 상세 정보
	public MemberDetResDto detFromEntity(){
		return MemberDetResDto.builder()
			.username(this.username)
			.nickname(this.nickname)
			.password(this.password)
			.email(this.email)
			.age(this.age)
			.phone_number(this.phone_number)
			.profile_image(this.profileImage)
			.build();
	}

	public MemberRankingResDto rankingFromEntity(){
		return MemberRankingResDto.builder()
			.nickname(this.nickname)
			.point(this.point)
			// .grade(this.grade)
			.reviewCount(this.reviews.size())
			.build();
	}

}
