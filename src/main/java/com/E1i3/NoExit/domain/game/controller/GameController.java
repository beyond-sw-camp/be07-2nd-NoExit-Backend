package com.E1i3.NoExit.domain.game.controller;

import java.time.LocalTime;
import java.util.List;

import com.E1i3.NoExit.domain.game.dto.GameDetailResDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.E1i3.NoExit.domain.common.dto.CommonResDto;
import com.E1i3.NoExit.domain.game.dto.GameResDto;
import com.E1i3.NoExit.domain.game.service.GameService;

@RestController
public class GameController {
	private final GameService gameService;

	@Autowired
	public GameController(GameService gameService) {
		this.gameService = gameService;
	}

	@GetMapping("/game/list")
	public ResponseEntity<CommonResDto> gameList(
			@PageableDefault(size=16, sort = "createdTime", direction = Sort.Direction.DESC)
			Pageable pageable) {
		Page<GameResDto> games = gameService.gameList(pageable);
		System.out.println("test");
		return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "OK", games), HttpStatus.OK);
	}
	@GetMapping("/game/detail/{id}")
	@Operation(summary = "게임 상세 정보 조회 API")
	public ResponseEntity<CommonResDto> getGameDetail(@PathVariable Long id) {
		GameDetailResDto gameDetailResDto = gameService.getGameDetail(id);
		CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "게임 상세 조회 완료", gameDetailResDto);
		return new ResponseEntity<>(commonResDto, HttpStatus.OK);
	}

	// store의 오프닝 시간대을 게임에서 출력하기 위한 코드
	@GetMapping("/game/{gameId}/available-hours")
	public ResponseEntity<List<LocalTime>> getAvailableHours(@PathVariable Long gameId) {
		List<LocalTime> availableHours = gameService.getAvailableHours(gameId);
		return ResponseEntity.ok(availableHours);
	}

	@GetMapping("/game/ranking")
	public ResponseEntity<?> gameRankingList() {
		List<GameResDto> games = gameService.gameList();
		return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "OK", games), HttpStatus.OK);
	}
}
