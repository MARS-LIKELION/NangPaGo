package com.mars.NangPaGo.domain.community.controller;

import com.mars.NangPaGo.common.aop.auth.AuthenticatedUser;
import com.mars.NangPaGo.common.component.auth.AuthenticationHolder;
import com.mars.NangPaGo.common.dto.PageDto;
import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.domain.community.dto.CommunityRequestDto;
import com.mars.NangPaGo.domain.community.dto.CommunityResponseDto;
import com.mars.NangPaGo.domain.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "커뮤니티 API", description = "커뮤니티 '게시물' 관련 API")
@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;

    @Operation(summary = "게시물 목록 조회")
    @GetMapping("/list")
    public ResponseDto<PageDto<CommunityResponseDto>> list(
        @RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "5") int pageSize) {

        String email = AuthenticationHolder.getCurrentUserEmail();
        return ResponseDto.of(communityService.pagesByCommunity(pageNo, pageSize, email));
    }

    @Operation(summary = "게시물 작성")
    @AuthenticatedUser
    @PostMapping
    public ResponseDto<CommunityResponseDto> create(
        @RequestBody @Valid CommunityRequestDto requestDto
        ) {

        String email = AuthenticationHolder.getCurrentUserEmail();
        return ResponseDto.of(communityService.createCommunity(requestDto, email), "게시물이 성공적으로 생성되었습니다.");
    }

    @Operation(summary = "게시물 수정")
    @AuthenticatedUser
    @PutMapping("/{id}")
    public ResponseDto<CommunityResponseDto> update(
        @RequestBody @Valid CommunityRequestDto requestDto,
        @PathVariable("id") Long id) {

        String email = AuthenticationHolder.getCurrentUserEmail();
        return ResponseDto.of(communityService.updateCommunity(id, requestDto, email), "게시물이 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "게시물 삭제")
    @AuthenticatedUser
    @DeleteMapping("/{id}")
    public ResponseDto<Void> delete(@PathVariable("id") Long id) {
        String email = AuthenticationHolder.getCurrentUserEmail();
        communityService.deleteCommunity(id, email);
        return ResponseDto.of(null, "게시물이 성공적으로 삭제되었습니다.");
    }
}
