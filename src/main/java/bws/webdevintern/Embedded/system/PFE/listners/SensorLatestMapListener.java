package bws.webdevintern.Embedded.system.PFE.listners;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;

import bws.webdevintern.Embedded.system.PFE.models.SensorInput;
import bws.webdevintern.Embedded.system.PFE.sse.SensorSseService;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SensorLatestMapListener implements EntryAddedListener<String, SensorInput>, EntryUpdatedListener<String, SensorInput> {

		    private static final String LATEST_MAP = "sensor-latest";

		    @Autowired
		    private HazelcastInstance hazelcastInstance;

		    @Autowired
		    private SensorSseService sseService;

		    @PostConstruct
		    public void registerListener() {
		        IMap<String, SensorInput> map = hazelcastInstance.getMap(LATEST_MAP);
		        map.addEntryListener(this, true); // true = includeValue, needed to read the payload
		    }

		    @Override
		    public void entryAdded(EntryEvent<String, SensorInput> event) {
		        publish(event.getValue());
		    }
		    
		    @Override
		    public void entryUpdated(EntryEvent<String, SensorInput> event) {
		        publish(event.getValue());
		    }

		    private void publish(SensorInput input) {
		        if (input != null && input.getSensor() != null) {
		            sseService.pushToSensor(input.getSensor().getId(), input);
		        }
		    }
}
