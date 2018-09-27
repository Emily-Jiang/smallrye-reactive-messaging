package io.smallrye.reactive.messaging.ack;

import javax.enterprise.inject.se.SeContainer;

import io.smallrye.reactive.messaging.WeldTestBaseWithoutTails;
import org.junit.Test;

import static io.smallrye.reactive.messaging.ack.BeanWithSubscribers.AUTO_ACKNOWLEDGMENT;
import static io.smallrye.reactive.messaging.ack.BeanWithSubscribers.MANUAL_ACKNOWLEDGMENT;
import static io.smallrye.reactive.messaging.ack.BeanWithSubscribers.NO_ACKNOWLEDGMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class SubscriberAcknowledgementTest extends WeldTestBaseWithoutTails {

  @Test
  public void testManualAcknowledgementWithMethodReturningSubscriber() {
    initializer.addBeanClasses(BeanWithSubscribers.class);
    SeContainer container = initializer.initialize();
    BeanWithSubscribers bean = container.select(BeanWithSubscribers.class).get();
    await().until(() -> bean.acknowledged(MANUAL_ACKNOWLEDGMENT).size() == 5);
    assertThat(bean.acknowledged(MANUAL_ACKNOWLEDGMENT)).containsExactly("a", "b", "c", "d", "e");
    assertThat(bean.received(MANUAL_ACKNOWLEDGMENT)).containsExactly("a", "b", "c", "d", "e");
  }

  @Test
  public void testNoAcknowledgementWithMethodReturningSubscriber() {
    initializer.addBeanClasses(BeanWithSubscribers.class);
    SeContainer container = initializer.initialize();
    BeanWithSubscribers bean = container.select(BeanWithSubscribers.class).get();
    await().until(() -> bean.received(NO_ACKNOWLEDGMENT).size() == 5);
    assertThat(bean.acknowledged(NO_ACKNOWLEDGMENT)).isNull();
    assertThat(bean.received(NO_ACKNOWLEDGMENT)).containsExactly("a", "b", "c", "d", "e");
  }

  @Test
  public void testAutoAcknowledgementWithMethodReturningSubscriber() {
    initializer.addBeanClasses(BeanWithSubscribers.class);
    SeContainer container = initializer.initialize();
    BeanWithSubscribers bean = container.select(BeanWithSubscribers.class).get();
    await().until(() -> bean.acknowledged(AUTO_ACKNOWLEDGMENT).size() == 5);
    assertThat(bean.acknowledged(AUTO_ACKNOWLEDGMENT)).containsExactly("a", "b", "c", "d", "e");
    assertThat(bean.received(AUTO_ACKNOWLEDGMENT)).containsExactly("a", "b", "c", "d", "e");
  }



}
