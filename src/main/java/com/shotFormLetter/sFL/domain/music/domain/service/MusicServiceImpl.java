package com.shotFormLetter.sFL.domain.music.domain.service;

import com.shotFormLetter.sFL.domain.music.domain.dto.MusicInfo;

import com.shotFormLetter.sFL.domain.music.domain.dto.MusicList;
import com.shotFormLetter.sFL.domain.music.domain.entity.Music;
import com.shotFormLetter.sFL.domain.music.domain.repository.MusicRepository;
import com.shotFormLetter.sFL.domain.post.s3.service.s3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MusicServiceImpl implements MusicService{
    private final MusicRepository musicRepository;
    private final s3UploadService s3UploadService;

    @Override
    public List<MusicList> getmusicList(){
        List<Music> musics=musicRepository.findAll();
        List<MusicList> musicList=new ArrayList<>();
        for(Music music:musics){
            MusicList getMusic=new MusicList();
            getMusic.setMusicNumber(music.getMusicNumber());
            getMusic.setMusicName(music.getMusicName());
            getMusic.setMusicLink(music.getMusicLink());
            musicList.add(getMusic);
        }
        return musicList;
    }
    @Override
    public void getObjectUrl(MultipartFile musicFile,Integer musicId){
        String objectUrl=s3UploadService.uploadMusic(musicFile);
        Music music=musicRepository.getMusicByMusicNumber(musicId);
        if (music==null){
            throw new IllegalStateException("전송실패");
        }
        music.setMusicLink(objectUrl);
        musicRepository.save(music);
    }
    @Override
    public MusicInfo getUserMusicInfo(Integer musicId){
        Music music=musicRepository.getMusicByMusicNumber(musicId);
        if (music==null){
            throw new IllegalStateException("음악 없음");
        }
        MusicInfo musicInfo=new MusicInfo();
        musicInfo.setMusicNumber(music.getMusicNumber());
        musicInfo.setMusicName(music.getMusicName());
        musicInfo.setMusicLink(music.getMusicLink());
        return musicInfo;
    }
    @Override
    public Integer createMusicInfo(String musicName){
        Music music=new Music();
        music.setMusicName(musicName);
        music=musicRepository.save(music);
        Integer musicId = music.getMusicNumber();
        return musicId;
    }
}
