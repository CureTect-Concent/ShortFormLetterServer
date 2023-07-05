package com.shotFormLetter.sFL.domain.music.domain.entity;

import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_number")
    private Integer musicNumber;

    @Column(name="music_name")
    private String musicName;

    @Column(name="music_link")
    private String musicLink;
}
