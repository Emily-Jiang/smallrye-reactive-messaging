package io.smallrye.reactive.messaging.amqp;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;

import io.reactivex.Flowable;

@ApplicationScoped
public class ProducingBean {

    @Incoming("data")
    @Outgoing("sink")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    public Message<Integer> process(Message<Integer> input) {
        return Message.of(input.getPayload() + 1, input::ack);
    }

    @Outgoing("data")
    public Publisher<Integer> source() {
        return Flowable.range(0, 10);
    }

    @Produces
    public Config myConfig() {
        String prefix = "mp.messaging.outgoing.sink.";
        Map<String, Object> config = new HashMap<>();
        config.put(prefix + "address", "sink");
        config.put(prefix + "connector", AmqpConnector.CONNECTOR_NAME);
        config.put(prefix + "host", System.getProperty("amqp-host"));
        config.put(prefix + "port", Integer.valueOf(System.getProperty("amqp-port")));
        config.put(prefix + "durable", true);
        if (System.getProperty("amqp-user") != null) {
            config.put(prefix + "username", System.getProperty("amqp-user"));
            config.put(prefix + "password", System.getProperty("amqp-pwd"));
        }
        return new MapBasedConfig(config);
    }

}
