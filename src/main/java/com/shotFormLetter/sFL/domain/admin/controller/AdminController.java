package com.shotFormLetter.sFL.domain.admin.controller;


import com.shotFormLetter.sFL.domain.admin.controller.dto.request.BenUserForm;
import com.shotFormLetter.sFL.domain.admin.domain.service.AdminService;
import com.shotFormLetter.sFL.domain.member.dto.request.LoginDto;
import com.shotFormLetter.sFL.domain.member.dto.request.MemberDto;
import com.shotFormLetter.sFL.domain.member.dto.response.TokenUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    @PostMapping("/join")
    public void AdminJoin(@Valid @RequestBody MemberDto memberDto) {
        adminService.join(memberDto);
    }


    @PostMapping("/login")
    public ResponseEntity<?> AdminLogin(@Valid @RequestBody LoginDto loginDto) {
        TokenUser tokenUser=adminService.login(loginDto);
        return ResponseEntity.ok(tokenUser);
    }

    @PutMapping("/block")
    public void blockMember(@RequestHeader("X-AUTH-TOKEN")String token,@Valid @RequestBody BenUserForm benUser){
        adminService.userBen(benUser,token);
    }

    @PutMapping("/unblock")
    public void unblockMember(@RequestHeader("X-AUTH-TOKEN")String token, @Valid @RequestBody BenUserForm benUserForm){
        adminService.unLockUser(benUserForm,token);
    }

    @PutMapping("/recheck")
    public void recheck(@RequestHeader("X-AUTH-TOKEN")String token, @Valid @RequestBody BenUserForm benUserForm){
        adminService.changeReportStatus(benUserForm, token);
    }
}
