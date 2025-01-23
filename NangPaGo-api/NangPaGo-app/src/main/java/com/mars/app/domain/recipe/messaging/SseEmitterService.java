package com.mars.app.domain.recipe.messaging;

import static java.lang.Long.MAX_VALUE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
public class SseEmitterService {

    private static final String EVENT_NAME = "좋아요 이벤트 발생";
    private final ConcurrentHashMap<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(Long recipeId) {
        SseEmitter emitter = new SseEmitter(MAX_VALUE);
        addEmitterToMap(recipeId, emitter);
        registerEmitterCallbacks(recipeId, emitter);
        return emitter;
    }

    public void sendLikeCount(Long recipeId, int likeCount) {
        try {
            List<SseEmitter> recipeEmitters = getEmitterForRecipe(recipeId);
            recipeEmitters.removeIf(emitter -> !sendEventToEmitter(emitter, likeCount));
        } catch (Exception e) {
            log.error("좋아요 이벤트 전송 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private void addEmitterToMap(Long recipeId, SseEmitter emitter) {
        emitters.computeIfAbsent(recipeId, key -> new ArrayList<>()).add(emitter);
    }

    private void registerEmitterCallbacks(Long recipeId, SseEmitter emitter) {
        emitter.onCompletion(() -> removeEmitter(recipeId, emitter));
    }

    private boolean sendEventToEmitter(SseEmitter emitter, int likeCount) {
        try {
            emitter.send(SseEmitter.event().name(EVENT_NAME).data(likeCount));
            return true;
        } catch (Exception e) {
            log.error("좋아요 수 전송 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    private List<SseEmitter> getEmitterForRecipe(Long recipeId) {
        return emitters.getOrDefault(recipeId, Collections.emptyList());
    }

    private void removeEmitter(Long recipeId, SseEmitter emitter) {
        emitters.computeIfPresent(recipeId, (key, emittersList) -> {
            emittersList.remove(emitter);
            if (emittersList.isEmpty()) {
                emitters.remove(recipeId);
                return null;
            }
            return emittersList;
        });
    }
}
