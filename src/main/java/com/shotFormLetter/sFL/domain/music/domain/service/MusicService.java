package com.shotFormLetter.sFL.domain.music.domain.service;

import com.shotFormLetter.sFL.domain.music.domain.dto.MusicInfo;
import com.shotFormLetter.sFL.domain.music.domain.dto.MusicList;
import com.shotFormLetter.sFL.domain.music.domain.entity.Music;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MusicService {
    List<MusicList> getmusicList();
    void getObjectUrl(MultipartFile musicFile,Integer musicId);
    MusicInfo getUserMusicInfo(Integer musicId);

    Integer createMusicInfo(String musicName);

}
