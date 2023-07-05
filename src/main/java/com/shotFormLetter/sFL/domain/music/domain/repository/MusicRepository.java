package com.shotFormLetter.sFL.domain.music.domain.repository;

import com.shotFormLetter.sFL.domain.music.domain.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface MusicRepository extends JpaRepository<Music, Integer> {
    Music getMusicByMusicNumber(Integer musicNumber);

    List<Music> findAll();
}
