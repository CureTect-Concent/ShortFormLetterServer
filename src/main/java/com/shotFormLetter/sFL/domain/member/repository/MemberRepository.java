package com.shotFormLetter.sFL.domain.member.repository;

import com.shotFormLetter.sFL.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
//    Optional<Member> findByUserId(String userId);
//
//    Optional<Member> findByUserName(String userId);

    Optional<Member> findByUserId(String userId);
    Optional<Member> findByUserName(String userName);
    Optional<Member> findById(Long id);

    Optional<Member> findByRefreshToken(String refreshToken);
    Member getByUserName(String userName);

    Member getById(Long id);
//
//    Optional<Member> getUserName(String userId);
//    Optional<Member> findByUserId(Long id);
}