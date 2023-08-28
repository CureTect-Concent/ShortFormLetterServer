package com.shotFormLetter.sFL.domain.statistics.domain.repository;

import com.shotFormLetter.sFL.domain.post.domain.entity.Post;
import com.shotFormLetter.sFL.domain.statistics.domain.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatisticsRepository extends JpaRepository<Statistics,Long> {

//    Statistics getStatisticsByPost(Post post);

    Statistics getByPostTimeLineId(Long postTimeLineId);

    Statistics getByMemberId(Long memberId);


    List<Statistics> getListByMemberId(Long memberId);
}
