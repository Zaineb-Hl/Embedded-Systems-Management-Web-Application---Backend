package bws.webdevintern.Embedded.system.PFE.sse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import bws.webdevintern.Embedded.system.PFE.models.SensorInput;
@Service
public class SensorSseService {

	// sensorId -> emitters currently watching it (a sensor can be open in several tabs)
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    private static final long TIMEOUT = 30 * 60 * 1000L; // 30 min, tune to taste

    public SseEmitter subscribe(Long sensorId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitters.computeIfAbsent(sensorId, id -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(sensorId, emitter));
        emitter.onTimeout(() -> remove(sensorId, emitter));
        emitter.onError(e -> remove(sensorId, emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            remove(sensorId, emitter);
        }
        return emitter;
    }

    public void pushToSensor(Long sensorId, SensorInput input) {
        List<SseEmitter> list = emitters.get(sensorId);
        if (list == null || list.isEmpty()) return;

        for (SseEmitter emitter : list) {
            try {
                emitter.send(SseEmitter.event().name("sensor-update").data(input));
            } catch (IOException | IllegalStateException e) {
                remove(sensorId, emitter);
            }
        }
    }

    private void remove(Long sensorId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(sensorId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) emitters.remove(sensorId);
        }
    }
}
