package bws.webdevintern.Embedded.system.PFE.controllers;

import bws.webdevintern.Embedded.system.PFE.sse.SensorSseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sensors")
public class SensorSseController {
    @Autowired
    private SensorSseService sseService;

    @GetMapping(value = "/{id}/stream", produces = "text/event-stream")
    public SseEmitter streamSensor(@PathVariable Long id) {
        return sseService.subscribe(id);
    }

}
