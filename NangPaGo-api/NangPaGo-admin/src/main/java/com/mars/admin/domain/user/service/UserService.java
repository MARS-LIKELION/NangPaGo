package com.mars.admin.domain.user.service;

import com.mars.admin.domain.user.repository.UserRepository;
import com.mars.common.dto.user.UserResponseDto;
import com.mars.common.exception.NPGExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;

  public UserResponseDto getCurrentUser(String email) {
    return UserResponseDto.from(userRepository.findByEmail(email)
        .orElseThrow(NPGExceptionType.NOT_FOUND_USER::of));
  }

}
