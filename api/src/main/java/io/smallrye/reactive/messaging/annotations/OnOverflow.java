package io.smallrye.reactive.messaging.annotations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.smallrye.reactive.messaging.Emitter;

/**
 * Allows configuring the back pressure policy on injected {@link Emitter}:
 *
 * <pre>
 * {
 *     &#64;code
 *     &#64;Channel("channel")
 *     &#64;OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 256)
 *     Emitter<String> emitter;
 * }
 * </pre>
 *
 * </pre>
 * <p>
 * When not used, a {@link OnOverflow.Strategy#BUFFER} strategy is used with a buffer limited to 128 elements.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD, CONSTRUCTOR, FIELD, PARAMETER })
public @interface OnOverflow {

    /**
     * The back pressure strategy.
     */
    enum Strategy {
        /**
         * Buffers <strong>all</strong> values until the downstream consumes it.
         * This creates a buffer with the size specified by {@link #bufferSize()} if present.
         * Otherwise, the size will be the value of the config property
         * <strong>mp.messaging.emitter.defult-buffer-size</strong>.
         * If the buffer is full, an error is propagated.
         */
        BUFFER,
        /**
         * Buffers <strong>all</strong> values until the downstream consumes it.
         * This creates an unbound buffer. If the buffer is full, an error is propagated.
         */
        UNBOUNDED_BUFFER,
        /**
         * Drops the most recent value if the downstream can't keep up. It means that new value emitted by the upstream
         * are ignored.
         */
        DROP,

        /**
         * Propagates a failure in case the downstream can't keep up.
         */
        FAIL,

        /**
         * Keeps only the latest value, dropping any previous value if the downstream can't keep up.
         */
        LATEST,

        /**
         * The values are propagated without any back pressure strategy. It's the responsibility from the downstream to
         * implement a strategy to deal with overflow.
         */
        NONE
    }

    /**
     * @return the name of the strategy to be used on overflow.
     */
    Strategy value();

    /**
     * @return the size of the buffer when {@link Strategy#BUFFER} is used. If not set and if the {@link Strategy#BUFFER}
     *         strategy is used, the buffer size will be defaulted to the value of the config property
     *         mp.messaging.emitter.defult-buffer-size.
     */
    long bufferSize() default 0;

}
