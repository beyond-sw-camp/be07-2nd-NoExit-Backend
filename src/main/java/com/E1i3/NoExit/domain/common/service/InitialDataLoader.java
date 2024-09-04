<<<<<<< HEAD
//package com.E1i3.NoExit.domain.common.service;
//
//import com.E1i3.NoExit.domain.grade.domain.Grade;
//import com.E1i3.NoExit.domain.grade.repository.GradeRepository;
//import com.E1i3.NoExit.domain.member.domain.Member;
//import com.E1i3.NoExit.domain.member.domain.Role;
//import com.E1i3.NoExit.domain.member.repository.MemberRepository;
//import com.E1i3.NoExit.domain.owner.domain.Owner;
//import com.E1i3.NoExit.domain.owner.repository.OwnerRepository;
//
//import org.hibernate.annotations.Check;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
////테스트 용이성을 위한 InitialDataLoader
//@Component
//public class InitialDataLoader implements CommandLineRunner {
//
//	@Autowired
//	private OwnerRepository ownerRepository;
//
//	@Autowired
//	private MemberRepository memberRepository;
//
//	@Autowired
//	private PasswordEncoder passwordEncoder;
//	@Autowired
//	private GradeRepository gradeRepository;
//
//	@Override
//	public void run(String... args) throws Exception {
//
//		Grade grade = Grade.builder().id(1L)
//			.gradeName("초보 탈출꾼").build();
//		gradeRepository.save(grade);
//		grade = Grade.builder().id(2L)
//			.gradeName("상급 탈출꾼").build();
//		gradeRepository.save(grade);
//		grade = Grade.builder().id(3L)
//			.gradeName("탈출 전문가").build();
//		gradeRepository.save(grade);
//		grade = Grade.builder().id(4L)
//			.gradeName("탈출 마스터").build();
//		gradeRepository.save(grade);
//		grade = Grade.builder().id(5L)
//			.gradeName("전설의 탈출꾼").build();
//		gradeRepository.save(grade);
//
//		grade = gradeRepository.findById(1L).get();
//
//		// Check and create OWNER account if not exis
//		// ts
//		if (ownerRepository.findByEmail("owner@test.com").isEmpty()) {
//			Owner admin = Owner.builder()
//				.username("owner")
//				.email("owner@test.com")
//				.password(passwordEncoder.encode("12341234")) // Encode password
//				.storeName("Admin Store") // Assuming storeName is required for Owner
//				.phoneNumber("010-1234-5678") // Example phone number
//				.role(Role.OWNER) // Ensure Role is set appropriately
//				.build();
//			ownerRepository.save(admin);
//		}
//
//		if (ownerRepository.findByEmail("test@test.com").isEmpty()) {
//			Owner test = Owner.builder()
//				.username("test")
//				.email("test@test.com")
//				.password("12341234")
//				.storeName("홍대의 유명한 추리 테마 방탈출")
//				.phoneNumber("010-9999-9999")
//				.role(Role.OWNER)
//				.build();
//			ownerRepository.save(test);
//		}
//
//		if (memberRepository.findByEmail("user@test.com").isEmpty()) {
//			Member user = Member.builder()
//					.username("user")
//					.email("user@test.com")
//					.password(passwordEncoder.encode("12341234")) // Encode password
//					.role(Role.USER)
//					.age(23)
//					.grade(grade)
//					.phone_number("010-1234-5678") // Example phone number
//					.nickname("user") // Example nickname
//					.profileImage(
//							"https://noexit-bucket.s3.ap-northeast-2.amazonaws.com/member/a547286f-7f94-4983-9856-c68039e1e515.png\n") // Set default or placeholder image URL
//					.build();
//			memberRepository.save(user);
//		}
//		if (memberRepository.findByEmail("user2@test.com").isEmpty()) {
//			Member user = Member.builder()
//					.username("user_test2")
//					.email("user2@test.com")
//					.password(passwordEncoder.encode("12341234")) // Encode password
//					.role(Role.USER)
//					.age(48)
//					.grade(grade)
//					.phone_number("010-9876-9876") // Example phone number
//					.nickname("user_test2") // Example nickname
//					.profileImage(
//							"https://noexit-bucket.s3.ap-northeast-2.amazonaws.com/member/393e9081-b2f1-4809-a7ce-833bfcb79ab8.png\n") // Set default or placeholder image URL
//					.build();
//			memberRepository.save(user);
//		}
//
//		if (memberRepository.findByEmail("user@gmail.com").isEmpty()) {
//			for (int i = 3; i <= 100; i++) { // 1부터 100까지 반복
//				Member user = Member.builder()
//						.username("user" + i) // 고유한 username
//						.email("user" + i + "@gmail.com") // 고유한 email
//						.password(passwordEncoder.encode("12341234")) // 비밀번호는 동일
//						.role(Role.USER)
//						.age(23 + i % 30) // 예시로 나이를 조금 다르게 설정 (23~52)
//						.grade(grade)
//						.phone_number("010-1234-" + String.format("%04d", i)) // 고유한 전화번호
//						.nickname("user" + i) // 고유한 nickname
//						.profileImage(
//								"https://noexit-bucket.s3.ap-northeast-2.amazonaws.com/member/a547286f-7f94-4983-9856-c68039e1e515.png") // 프로필 이미지는 동일하게 설정
//						.build();
//				memberRepository.save(user);
//			}
//		}
//
//
//	}
//
//}
=======
package com.E1i3.NoExit.domain.common.service;

import com.E1i3.NoExit.domain.grade.domain.Grade;
import com.E1i3.NoExit.domain.grade.repository.GradeRepository;
import com.E1i3.NoExit.domain.member.domain.Member;
import com.E1i3.NoExit.domain.member.domain.Role;
import com.E1i3.NoExit.domain.member.repository.MemberRepository;
import com.E1i3.NoExit.domain.owner.domain.Owner;
import com.E1i3.NoExit.domain.owner.repository.OwnerRepository;

import org.hibernate.annotations.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

//테스트 용이성을 위한 InitialDataLoader
@Component
public class InitialDataLoader implements CommandLineRunner {

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private GradeRepository gradeRepository;

	@Override
	public void run(String... args) throws Exception {

		Grade grade = Grade.builder().id(1L)
			.gradeName("초보 탈출꾼").build();
		gradeRepository.save(grade);
		grade = Grade.builder().id(2L)
			.gradeName("상급 탈출꾼").build();
		gradeRepository.save(grade);
		grade = Grade.builder().id(3L)
			.gradeName("탈출 전문가").build();
		gradeRepository.save(grade);
		grade = Grade.builder().id(4L)
			.gradeName("탈출 마스터").build();
		gradeRepository.save(grade);
		grade = Grade.builder().id(5L)
			.gradeName("전설의 탈출꾼").build();
		gradeRepository.save(grade);

		grade = gradeRepository.findById(1L).get();

		// Check and create OWNER account if not exis
		// ts
		if (ownerRepository.findByEmail("owner@test.com").isEmpty()) {
			Owner admin = Owner.builder()
				.username("owner")
				.email("owner@test.com")
				.password(passwordEncoder.encode("12341234")) // Encode password
				.storeName("Admin Store") // Assuming storeName is required for Owner
				.phoneNumber("010-1234-5678") // Example phone number
				.role(Role.OWNER) // Ensure Role is set appropriately
				.build();
			ownerRepository.save(admin);
		}

		if (ownerRepository.findByEmail("test@test.com").isEmpty()) {
			Owner test = Owner.builder()
				.username("test")
				.email("test@test.com")
				.password("12341234")
				.storeName("홍대의 유명한 추리 테마 방탈출")
				.phoneNumber("010-9999-9999")
				.role(Role.OWNER)
				.build();
			ownerRepository.save(test);
		}

		if (memberRepository.findByEmail("user@test.com").isEmpty()) {
			Member user = Member.builder()
					.username("user")
					.email("user@test.com")
					.password(passwordEncoder.encode("12341234")) // Encode password
					.role(Role.USER)
					.age(23)
					.grade(grade)
					.phone_number("010-1234-5678") // Example phone number
					.nickname("user") // Example nickname
					.profileImage(
							"https://noexit-bucket.s3.ap-northeast-2.amazonaws.com/member/a547286f-7f94-4983-9856-c68039e1e515.png\n") // Set default or placeholder image URL
					.build();
			memberRepository.save(user);
		}
		if (memberRepository.findByEmail("user2@test.com").isEmpty()) {
			Member user = Member.builder()
					.username("user_test2")
					.email("user2@test.com")
					.password(passwordEncoder.encode("12341234")) // Encode password
					.role(Role.USER)
					.age(48)
					.grade(grade)
					.phone_number("010-9876-9876") // Example phone number
					.nickname("user_test2") // Example nickname
					.profileImage(
							"https://noexit-bucket.s3.ap-northeast-2.amazonaws.com/member/393e9081-b2f1-4809-a7ce-833bfcb79ab8.png\n") // Set default or placeholder image URL
					.build();
			memberRepository.save(user);
		}

		if (memberRepository.findByEmail("user@gmail.com").isEmpty()) {
			for (int i = 3; i <= 100; i++) { // 1부터 100까지 반복
				Member user = Member.builder()
						.username("user" + i) // 고유한 username
						.email("user" + i + "@gmail.com") // 고유한 email
						.password(passwordEncoder.encode("12341234")) // 비밀번호는 동일
						.role(Role.USER)
						.age(23 + i % 30) // 예시로 나이를 조금 다르게 설정 (23~52)
						.grade(grade)
						.phone_number("010-1234-" + String.format("%04d", i)) // 고유한 전화번호
						.nickname("user" + i) // 고유한 nickname
						.profileImage(
								"https://noexit-bucket.s3.ap-northeast-2.amazonaws.com/member/a547286f-7f94-4983-9856-c68039e1e515.png") // 프로필 이미지는 동일하게 설정
						.build();
				memberRepository.save(user);
			}
		}


	}

}
>>>>>>> parent of ef311cb (Merge pull request #118 from NoExit13/feat/main_pageing)
