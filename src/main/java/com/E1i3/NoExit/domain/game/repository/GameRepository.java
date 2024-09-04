package com.E1i3.NoExit.domain.game.repository;

import java.util.List;

import com.E1i3.NoExit.domain.game.domain.Game;
import com.E1i3.NoExit.domain.owner.domain.Owner;
import com.E1i3.NoExit.domain.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    
  List<Game> findByStoreIn(List<Store> stores);
  List<Game> findAll();

}
