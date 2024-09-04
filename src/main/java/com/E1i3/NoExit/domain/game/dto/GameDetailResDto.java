

package com.E1i3.NoExit.domain.game.dto;

import com.E1i3.NoExit.domain.game.domain.AgeLimit;
import com.E1i3.NoExit.domain.game.domain.Difficult;
import com.E1i3.NoExit.domain.game.domain.Game;
import com.E1i3.NoExit.domain.store.domain.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameDetailResDto {
    private Long id;
    private String gameName; // 게임명
    private String runningTime; // 러닝 타임
    private Difficult difficult; // 난이도
    private int price; // 가격
    private int maximunPerson; // 최대인원
    private String gameInfo; // 게임설명
    private AgeLimit ageLimit; // 나이제한
    private String imagePath; // 사진 경로
    private String storeName; // 상점 이름

    private Double latitude;  // 가게의 위도
    private Double longitude; // 가게의 경도


    public static GameDetailResDto fromEntity(Game game) {
        Store store = game.getStore();
        return GameDetailResDto.builder()
                .id(game.getId())
                .gameName(game.getGameName())
                .runningTime(game.getRunningTime())
                .difficult(game.getDifficult())
                .price(game.getPrice())
                .maximunPerson(game.getMaximunPerson())
                .gameInfo(game.getGameInfo())
                .ageLimit(game.getAgeLimit())
                .imagePath(game.getImagePath())
                .storeName(game.getStore().getStoreName())
                .latitude(store.getLatitude())  // 위도 정보 추가
                .longitude(store.getLongitude()) // 경도 정보 추가
                .build();
    }
}
