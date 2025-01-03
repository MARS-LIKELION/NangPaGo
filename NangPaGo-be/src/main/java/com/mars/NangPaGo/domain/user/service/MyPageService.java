package com.mars.NangPaGo.domain.user.service;

import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.user.dto.MyPageDto;
import com.mars.NangPaGo.domain.user.dto.MyPageSubQueryCountDto;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MyPageService {

    private final UserRepository userRepository;

    public MyPageDto myPage(String email) {

        User user = userRepository.findByEmail(email)
            .orElseThrow(NPGExceptionType.NOT_FOUND_USER::of);

        MyPageSubQueryCountDto countDto = userRepository.findUserStatsByEmail(email);

        return MyPageDto.of(user, countDto);
    }
}
