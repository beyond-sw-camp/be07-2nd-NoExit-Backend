package com.E1i3.NoExit.domain.reservation.controller;

import com.E1i3.NoExit.domain.common.dto.CommonResDto;
import com.E1i3.NoExit.domain.reservation.domain.Reservation;
import com.E1i3.NoExit.domain.reservation.dto.ReservationDetailResDto;
import com.E1i3.NoExit.domain.reservation.dto.ReservationListResDto;
import com.E1i3.NoExit.domain.reservation.dto.ReservationSaveDto;
import com.E1i3.NoExit.domain.reservation.dto.ReservationUpdateResDto;
import com.E1i3.NoExit.domain.reservation.service.ReservationService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@Api(tags="예약 서비스")
public class ReservationController {

    @Autowired
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

//    @GetMapping("/reservation/create")
//    public String reservationCreateForm() {
//        return "reservation/create";
//    }
    /* 예약 */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/reservation/create") // 생성
    @Operation(summary= "[일반 사용자] 예약생성 API")
    public ResponseEntity<?> reservationCreate(@RequestBody ReservationSaveDto dto) {
        Reservation reservation = reservationService.save(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "예약 신청을 완료하였습니다.", reservation);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    /* 유저별 자신의 예약 조회 */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/reservation/myreservation") // 이메일로 조회
    @Operation(summary= "[일반 사용자] 예약리스트 API")
    public ResponseEntity<?> reservationList() {
        List<ReservationListResDto> reservationListResDtos = reservationService.find();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "리스트 조회 완료!", reservationListResDtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /* 이메일과 예약 ID를 기준으로 예약 상세 조회 */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/reservation/detail/{id}")
    @Operation(summary= "[일반 사용자] 예약상세조회 API")
    public ResponseEntity<?> getReservationDetail(@PathVariable Long id) {
        ReservationDetailResDto reservationDetailResDto = reservationService.getReservationDetail(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "예약 상세 조회 완료!", reservationDetailResDto);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/reservation/storeReservation")
    public ResponseEntity<?> getReservationsByOwner() {
        List<ReservationDetailResDto> reservations = reservationService.findReservationsByOwner();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "예약 상세 조회 완료!", reservations);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /* 예약 승인 여부 처리*/
//    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/reservation/approval")
    @Operation(summary= "[점장 사용자] 예약 승인 여부 처리 API")
    public ResponseEntity<?> updateApprovalStatus(@RequestBody ReservationUpdateResDto dto) {
        try {
            Reservation updatedReservation = reservationService.updateApprovalStatus(dto);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "예약 승인이 완료되었습니다.", updatedReservation.getId());
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(new CommonResDto(HttpStatus.BAD_REQUEST, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    /*  예약 취소 */
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/reservation/delete/{id}")
    @Operation(summary= "[일반 사용자] 예약 취소 API")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        Reservation canceledReservation = reservationService.cancelReservation(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "예약 취소가 완료되었습니다.", canceledReservation.getId());

        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/reservation/available-hours")
    public ResponseEntity<List<String>> getAvailableHours(
            @RequestParam Long gameId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate resDate) {
        List<String> reservedHours = reservationService.findAvailableHours(gameId, resDate);
        return ResponseEntity.ok(reservedHours);
    }

    /* 특정 일 특정 시간대 에 대한 예약 여부 조회 */
    /* 후 일수에 관한 예약 가능 시간대로 변경 */
//    @GetMapping("/reservation/time")
//    public ResponseEntity<?> findReservation(@RequestParam String resDate, @RequestParam String resDateTime) {
//        Optional<Reservation> reservation = reservationService.findReservationTime(resDate, resDateTime);
//        if (reservation.isPresent()) {
//            return new ResponseEntity<>(reservation.get(), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(new CommonResDto(HttpStatus.NOT_FOUND, "시간대 별 조회가 불가능합니다.", null), HttpStatus.NOT_FOUND);
//        }
//    }





    //    @GetMapping("/reservation/list")
//    public String reservationList(Model model) {
//        return "reservation/list";
//    }
//
//    @GetMapping("/reservation/detail/{id}")
//    public ResponseEntity<ReservationDetailResDto> getReservationDetail(@PathVariable Long id) {
//        ReservationDetailResDto reservationDetail = reservationService.getReservationDetail(id);
//        return ResponseEntity.ok(reservationDetail);
//    }
//
//    @DeleteMapping("/reservation/{id}")
//    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
//        reservationService.delete(id);
//        return ResponseEntity.noContent().build();
//    } // 필요 여부에 따라 삭제
//    @PutMapping("/reservation/approval")
//    public ResponseEntity<?> reservationApprovalStatusUpdate(@RequestBody ReservationUpdateResDto dto) {
//        Reservation updatereservation = reservationService.updateApprovalStatus(dto);
//        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Approval status updated", updatereservation.getId());
//        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
//    }
    //    @PatchMapping("/reservation/{uuid}")
//    public ResponseEntity<?> ReservationApprovalStatusUpdate(@PathVariable UUID uuid,
//                                                             @RequestParam String adminEmail,
//                                                             @RequestParam ApprovalStatus status) {
//        Reservation reservation = reservationService.updateApprovalStatus(uuid, adminEmail, status);
//        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Approval status updated", reservation.getId());
//        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
//    }
}




