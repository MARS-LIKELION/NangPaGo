package com.mars.common.enums.user;

import static com.mars.common.exception.NPGExceptionType.BAD_REQUEST_INVALID_EVENTCODE;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserNotificationEventCode {

    // Like
    USER_RECIPE_LIKE("L01", "USER_RECIPE"),

    // Comment
    USER_RECIPE_COMMENT("C01", "USER_RECIPE"),

    ;

    private static final Map<String, UserNotificationEventCode> CODE_MAP = Stream.of(values())
        .collect(Collectors.toMap(UserNotificationEventCode::getCode, e -> e));

    private final String code;
    private final String postType;

    public static UserNotificationEventCode from(String code) {
        return Optional.ofNullable(CODE_MAP.get(code))
            .orElseThrow(() -> invalidEventCodeException(code));
    }

    private static RuntimeException invalidEventCodeException(String code) {
        return BAD_REQUEST_INVALID_EVENTCODE.of("잘못된 이벤트 코드: " + code);
    }
}
