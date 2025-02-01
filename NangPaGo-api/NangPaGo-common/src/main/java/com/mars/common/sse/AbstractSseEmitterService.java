package com.mars.common.sse;

import static java.lang.Long.MAX_VALUE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
public abstract class AbstractSseEmitterService<T> {
    private static final String CONNECTION_ERROR_MESSAGE = "SSE 연결이 끊김: {}";
    private static final String SSE_ERROR_MESSAGE = "SSE 에러 발생: {}";
    private static final String EVENT_ERROR_MESSAGE = "이벤트 전송 중 오류 발생: {}";

    private final ConcurrentHashMap<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(Long id) {
        SseEmitter emitter = new SseEmitter(MAX_VALUE);
        registerEmitter(id, emitter);
        return emitter;
    }

    public void sendToClient(Long id, T data) {
        try {
            List<SseEmitter> clientEmitters = getEmittersById(id);
            removeDeadEmittersAndSendData(clientEmitters, data);
        } catch (Exception e) {
            log.error(EVENT_ERROR_MESSAGE, e.getMessage(), e);
        }
    }

    protected abstract String getEventName();

    private void registerEmitter(Long id, SseEmitter emitter) {
        addEmitterToMap(id, emitter);
        setupEmitterCallbacks(id, emitter);
    }

    private void addEmitterToMap(Long id, SseEmitter emitter) {
        emitters.computeIfAbsent(id,
                key -> Collections.synchronizedList(new ArrayList<>()))
            .add(emitter);
    }

    private void setupEmitterCallbacks(Long id, SseEmitter emitter) {
        emitter.onCompletion(() -> removeEmitter(id, emitter));
        emitter.onTimeout(() -> removeEmitter(id, emitter));
        emitter.onError(e -> {
            log.debug(SSE_ERROR_MESSAGE, e.getMessage());
            removeEmitter(id, emitter);
        });
    }

    private List<SseEmitter> getEmittersById(Long id) {
        return emitters.getOrDefault(id,
            Collections.synchronizedList(new ArrayList<>()));
    }

    private void removeDeadEmittersAndSendData(List<SseEmitter> clientEmitters, T data) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        synchronized (clientEmitters) {
            processEmitters(clientEmitters, deadEmitters, data);
            clientEmitters.removeAll(deadEmitters);
        }
    }

    private void processEmitters(
        List<SseEmitter> clientEmitters,
        List<SseEmitter> deadEmitters,
        T data
    ) {
        for (SseEmitter emitter : clientEmitters) {
            if (!isEmitterValid(emitter) || !sendEventToEmitter(emitter, data)) {
                deadEmitters.add(emitter);
            }
        }
    }

    private boolean isEmitterValid(SseEmitter emitter) {
        return emitter != null;
    }

    private boolean sendEventToEmitter(SseEmitter emitter, T data) {
        try {
            emitter.send(createSseEvent(data));
            return true;
        } catch (Exception e) {
            log.debug(CONNECTION_ERROR_MESSAGE, e.getMessage());
            return false;
        }
    }

    private SseEmitter.SseEventBuilder createSseEvent(T data) {
        return SseEmitter.event()
            .name(getEventName())
            .data(data);
    }

    private void removeEmitter(Long id, SseEmitter emitter) {
        emitters.computeIfPresent(id, (key, emittersList) -> {
            emittersList.remove(emitter);
            return emittersList.isEmpty() ? null : emittersList;
        });
    }
}
