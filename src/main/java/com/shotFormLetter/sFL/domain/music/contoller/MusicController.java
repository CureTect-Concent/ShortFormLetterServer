package com.shotFormLetter.sFL.domain.music.contoller;

import com.shotFormLetter.sFL.domain.music.domain.dto.MusicList;
import com.shotFormLetter.sFL.domain.music.domain.service.MusicService;
import com.shotFormLetter.sFL.domain.post.domain.dto.response.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/music")
@RequiredArgsConstructor

public class MusicController {
    private final MusicService musicService;

    @GetMapping("/list")
    public List<MusicList> getList(){
        return musicService.getmusicList();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadMusic(@RequestParam("musicFile") MultipartFile newImageList,
                                         @RequestParam("musicName") String musicName){

        Integer musicId = musicService.createMusicInfo(musicName);
        musicService.getObjectUrl(newImageList, musicId);
        MessageDto messageDto=new MessageDto();
        messageDto.setMessage("전송완료");
        return ResponseEntity.ok(messageDto);
    }
}
