package io.smallrye.reactive.messaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.inject.se.SeContainer;

import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.beans.*;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.weld.exceptions.DefinitionException;
import org.junit.Test;
import org.reactivestreams.Subscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class SubscriberShapeTest extends WeldTestBaseWithoutTails {


  @Override
  public List<Class> getBeans() {
    return Collections.singletonList(SourceOnly.class);
  }

  @Test
  public void testBeanProducingASubscriberOfMessages() {
    initializer.addBeanClasses(BeanReturningASubscriberOfMessages.class);
    SeContainer container = initializer.initialize();
    BeanReturningASubscriberOfMessages collector = container.select(BeanReturningASubscriberOfMessages.class).get();
    assertThat(collector.payloads()).isEqualTo(EXPECTED);
  }

  @Test
  public void testBeanProducingASubscriberOfPayloads() {
    initializer.addBeanClasses(BeanReturningASubscriberOfPayloads.class);
    SeContainer container = initializer.initialize();
    BeanReturningASubscriberOfPayloads collector = container.select(BeanReturningASubscriberOfPayloads.class).get();
    assertThat(collector.payloads()).isEqualTo(EXPECTED);
  }

  @Test
  public void testThatWeCanProduceSubscriberOfMessage() {
    initializer.addBeanClasses(BeanReturningASubscriberOfMessagesButDiscarding.class);
    SeContainer container = initializer.initialize();
    assertThatSubscriberWasPublished(container);
  }

  @Test
  public void testThatWeCanConsumeMessagesFromAMethodReturningVoid() {
    // This case is not supported as it forces blocking acknowledgment.
    // See the MediatorConfiguration class for details.
    initializer.addBeanClasses(BeanConsumingMessagesAndReturningVoid.class);
    try {
      initializer.initialize();
      fail("Expected failure - method validation should have failed");
    } catch (DefinitionException e) {
      // Check we have the right cause
      assertThat(e).hasMessageContaining("Invalid method").hasMessageContaining("acknowledgment");
    }
  }

  @Test
  public void testThatWeCanConsumePayloadsFromAMethodReturningVoid() {
    initializer.addBeanClasses(BeanConsumingPayloadsAndReturningVoid.class);
    SeContainer container = initializer.initialize();
    BeanConsumingPayloadsAndReturningVoid collector = container.getBeanManager()
      .createInstance().select(BeanConsumingPayloadsAndReturningVoid.class).get();
    assertThat(collector.payloads()).isEqualTo(EXPECTED);
  }

  @Test
  public void testThatWeCanConsumeMessagesFromAMethodReturningSomething() {
    // This case is not supported as it forces blocking acknowledgment.
    // See the MediatorConfiguration class for details.

    initializer.addBeanClasses(BeanConsumingMessagesAndReturningSomething.class);
    try {
      initializer.initialize();
      fail("Expected failure - method validation should have failed");
    } catch (DefinitionException e) {
      // Check we have the right cause
      assertThat(e).hasMessageContaining("Invalid method").hasMessageContaining("acknowledgment");
    }
  }

  @Test
  public void testThatWeCanConsumePayloadsFromAMethodReturningSomething() {
    initializer.addBeanClasses(BeanConsumingPayloadsAndReturningSomething.class);
    SeContainer container = initializer.initialize();
    BeanConsumingPayloadsAndReturningSomething collector = container.getBeanManager()
      .createInstance().select(BeanConsumingPayloadsAndReturningSomething.class).get();
    assertThat(collector.payloads()).isEqualTo(EXPECTED);
  }

  @Test
  public void testThatWeCanConsumeMessagesFromAMethodReturningACompletionStage() {
    initializer.addBeanClasses(BeanConsumingMessagesAndReturningACompletionStageOfVoid.class);
    SeContainer container = initializer.initialize();
    BeanConsumingMessagesAndReturningACompletionStageOfVoid collector = container.getBeanManager()
      .createInstance().select(BeanConsumingMessagesAndReturningACompletionStageOfVoid.class).get();
    assertThat(collector.payloads()).isEqualTo(EXPECTED);
  }

  @Test
  public void testThatWeCanConsumePayloadsFromAMethodReturningACompletionStage() {
    initializer.addBeanClasses(BeanConsumingPayloadsAndReturningACompletionStageOfVoid.class);
    SeContainer container = initializer.initialize();
    BeanConsumingPayloadsAndReturningACompletionStageOfVoid collector = container.getBeanManager()
      .createInstance().select(BeanConsumingPayloadsAndReturningACompletionStageOfVoid.class).get();
    assertThat(collector.payloads()).isEqualTo(EXPECTED);
  }

  @Test
  public void testThatWeCanConsumeMessagesFromAMethodReturningACompletionStageOfSomething() {
    initializer.addBeanClasses(BeanConsumingMessagesAndReturningACompletionStageOfSomething.class);
    SeContainer container = initializer.initialize();
    BeanConsumingMessagesAndReturningACompletionStageOfSomething collector = container.getBeanManager()
      .createInstance().select(BeanConsumingMessagesAndReturningACompletionStageOfSomething.class).get();
    assertThat(collector.payloads()).isEqualTo(EXPECTED);
  }

  @Test
  public void testThatWeCanConsumePayloadsFromAMethodReturningACompletionStageOfSomething() {
    initializer.addBeanClasses(BeanConsumingPayloadsAndReturningACompletionStageOfSomething.class);
    SeContainer container = initializer.initialize();
    BeanConsumingPayloadsAndReturningACompletionStageOfSomething collector = container.getBeanManager()
      .createInstance().select(BeanConsumingPayloadsAndReturningACompletionStageOfSomething.class).get();
    assertThat(collector.payloads()).isEqualTo(EXPECTED);
  }


  @SuppressWarnings("unchecked")
  private void assertThatSubscriberWasPublished(SeContainer container) {
    assertThat(registry(container).getSubscriberNames()).contains("subscriber");
    Optional<Subscriber<? extends Message>> subscriber = registry(container).getSubscriber("subscriber");
    assertThat(subscriber).isNotEmpty();
    List<String> list = new ArrayList<>();
    Flowable.just("a", "b", "c").map(Message::of)
      .doOnNext(m -> list.add(m.getPayload()))
      .subscribe(((Subscriber<Message>) subscriber.orElseThrow(() -> new AssertionError("Subscriber should be present"))));
    assertThat(list).containsExactly("a", "b", "c");
  }


}
