package com.mars.NangPaGo.domain.community.service;

import com.mars.NangPaGo.domain.community.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
}
