package com.shotFormLetter.sFL.domain.statistics.domain.entity;

import com.shotFormLetter.sFL.domain.member.entity.Member;
import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
import io.swagger.models.auth.In;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statistics_Id")
    private Long StatisticsId;

    @Column(name="post_timeLine")
    private Long postTimeLineId;


    @Column(name="user_id")
    private Long memberId;


    @Column(name="view_time")
    @CollectionTable(name="statistics_time_line", joinColumns = @JoinColumn(name="post_id"))
    @ElementCollection
    private List<String> viewTime;


    @Column(name="total_view")
    private Integer view;
}

