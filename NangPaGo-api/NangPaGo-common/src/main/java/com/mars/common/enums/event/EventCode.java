package com.mars.common.enums.event;

import static com.mars.common.exception.NPGExceptionType.BAD_REQUEST_INVALID_EVENTCODE;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventCode {

    LIKE("A01"), FAVORITE("F02"), COMMENT("C01");

    private static final Map<String, EventCode> CODE_MAP = Stream.of(values())
        .collect(Collectors.toMap(EventCode::getCode, e -> e));

    private final String code;

    public static EventCode from(String code) {
        return Optional.ofNullable(CODE_MAP.get(code))
            .orElseThrow(() -> invalidEventCodeException(code));
    }

    private static RuntimeException invalidEventCodeException(String code) {
        return BAD_REQUEST_INVALID_EVENTCODE.of("잘못된 이벤트 코드: " + code);
    }
}
