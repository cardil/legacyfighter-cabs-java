package io.legacyfighter.cabs.geolocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

import static org.parboiled.common.Preconditions.checkArgument;

class RateLimit {
    private static final Logger LOG = LoggerFactory.getLogger(RateLimit.class);
    private final Duration duration;
    private final int maxPerDuration;
    private Instant lastOne;

    private int calls = 0;

    RateLimit(int maxPerDuration, Duration duration) {
        checkArgument(maxPerDuration > 0, "maxPerDuration must be positive");
        this.maxPerDuration = maxPerDuration;
        this.duration = duration;
    }

    <T> T execute(Supplier<T> supplier) {
        try {
            if (lastOne == null) {
                return supplier.get();
            }
            Instant now = Instant.now();
            if (lastOne.plus(duration).isBefore(now)) {
                return supplier.get();
            }
            long remaining = lastOne.plus(duration).toEpochMilli() - now.toEpochMilli();

            if (calls >= maxPerDuration) {
                LOG.info("Rate limit ({} per {}) exceeded, waiting {} ms",
                    maxPerDuration, duration, remaining);
                Thread.sleep(remaining);
            }
            return supplier.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } finally {
            calls++;
            lastOne = Instant.now();
        }
    }
}
