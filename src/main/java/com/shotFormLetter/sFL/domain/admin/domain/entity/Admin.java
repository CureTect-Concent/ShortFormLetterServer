package com.shotFormLetter.sFL.domain.admin.domain.entity;


import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    @Column(length = 100, nullable = false, unique = true , name = "admin_userid")
    private String adminUserId;

    @Column(length = 100, nullable = false, unique = true, name = "admin_name")
    private String adminName;

    @Column(length = 300, nullable = false, name = "admin_password")
    private String adminPassword;

    @Column(name = "admin_refresh_token")
    private String adminRefreshToken;


    @ElementCollection(fetch = FetchType.EAGER) //roles 컬렉션
    @Builder.Default
    @Column(name="admin_roles")
    private List<String> adminRoles = new ArrayList<>();


    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.adminRoles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }



}
