package com.mars.app.domain.community.controller;

import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.common.dto.page.PageDto;
import com.mars.common.dto.ResponseDto;
import com.mars.app.domain.community.dto.CommunityRequestDto;
import com.mars.app.domain.community.dto.CommunityResponseDto;
import com.mars.app.domain.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Tag(name = "커뮤니티 API", description = "커뮤니티 '게시물' 관련 API")
@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;

    @Operation(summary = "게시물 단일 조회")
    @GetMapping("/{id}")
    public ResponseDto<CommunityResponseDto> getCommunityById(@PathVariable Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityService.getCommunityById(id, userId));
    }

    @Operation(summary = "게시물 목록 조회")
    @GetMapping("/list")
    public ResponseDto<PageDto<CommunityResponseDto>> list(
        @RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "10") int pageSize) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityService.pagesByCommunity(pageNo, pageSize, userId));
    }

    @Operation(summary = "수정 페이지용 게시물 조회")
    @AuthenticatedUser
    @GetMapping("/edit/{id}")
    public ResponseDto<CommunityResponseDto> getPostForEdit(@PathVariable Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityService.getPostForEdit(id, userId), "게시물을 성공적으로 가져왔습니다.");
    }

    @Operation(summary = "게시물 작성")
    @AuthenticatedUser
    @PostMapping
    public ResponseDto<CommunityResponseDto> create(
        @ModelAttribute @Valid CommunityRequestDto requestDto,
        @RequestParam(value = "file", required = false) MultipartFile file
    ) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityService.createCommunity(requestDto, file, userId), "게시물이 성공적으로 생성되었습니다.");
    }

    @Operation(summary = "게시물 수정")
    @AuthenticatedUser
    @PutMapping("/{id}")
    public ResponseDto<CommunityResponseDto> update(
        @ModelAttribute @Valid CommunityRequestDto requestDto,
        @RequestParam(value = "file", required = false) MultipartFile file,
        @PathVariable("id") Long id) {

        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(communityService.updateCommunity(id, requestDto, file, userId), "게시물이 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "게시물 삭제")
    @AuthenticatedUser
    @DeleteMapping("/{id}")
    public ResponseDto<Void> delete(@PathVariable("id") Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        communityService.deleteCommunity(id, userId);
        return ResponseDto.of(null, "게시물이 성공적으로 삭제되었습니다.");
    }
}
