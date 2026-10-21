package jp.vemi.jsoncmapper.regression;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.*;
import jp.vemi.jsoncmapper.JsoncMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/** Regression coverage for jackson-databind #6227 through JsoncMapper. */
@Timeout(60)
public class SharedMetadataRegressionTest {
    public static class Probe {
        public final String id;
        public final int count;
        @JsonCreator
        public Probe(@JsonProperty("id") String id, @JsonProperty("count") int count) {
            this.id = id;
            this.count = count;
        }
    }

    public static class ValueProbe {
        @JsonValue public String value = "value";
        @JsonValue public String getValue() { return value; }
    }

    public static class KeyProbe {
        @JsonKey public String key = "key";
        @JsonKey public String getKey() { return key; }
    }

    public static class PublicView { }
    @JsonView(PublicView.class)
    public static class ViewedProbe { public String value = "visible"; }

    @FunctionalInterface
    interface CheckedAction { void run() throws Exception; }

    private static void concurrently(CheckedAction action) throws Exception {
        int threads = 16;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CyclicBarrier start = new CyclicBarrier(threads);
        List<Future<?>> futures = new ArrayList<>();
        try {
            for (int i = 0; i < threads; i++) {
                futures.add(pool.submit(() -> {
                    start.await(10, TimeUnit.SECONDS);
                    action.run();
                    return null;
                }));
            }
            for (Future<?> future : futures) { future.get(10, TimeUnit.SECONDS); }
        } finally {
            pool.shutdownNow();
            assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
        }
    }

    @Test
    void sharedFindPropertiesInitializesConsistently() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        for (int round = 0; round < 50; round++) {
            BeanDescription shared = mapper.getSerializationConfig().introspect(mapper.constructType(Probe.class));
            concurrently(() -> {
                List<BeanPropertyDefinition> properties = shared.findProperties();
                assertEquals(2, properties.size());
                assertTrue(properties.stream().anyMatch(p -> p.getName().equals("id")));
                assertTrue(properties.stream().anyMatch(p -> p.getName().equals("count")));
            });
        }
    }

    @Test
    void sharedJsonValueResolutionAlwaysSelectsGetter() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        for (int round = 0; round < 50; round++) {
            BeanDescription shared = mapper.getSerializationConfig().introspect(mapper.constructType(ValueProbe.class));
            // Collect first: the accessor race occurs after collection as well.
            shared.findProperties();
            concurrently(() -> assertTrue(shared.findJsonValueAccessor() instanceof AnnotatedMethod));
        }
    }

    @Test
    void sharedJsonKeyResolutionAlwaysSelectsGetter() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        for (int round = 0; round < 50; round++) {
            BeanDescription shared = mapper.getSerializationConfig().introspect(mapper.constructType(KeyProbe.class));
            shared.findProperties();
            concurrently(() -> assertTrue(shared.findJsonKeyAccessor() instanceof AnnotatedMethod));
        }
    }

    @Test
    void sharedDefaultViewsNeverPublishesUnresolvedNull() throws Exception {
        CountDownLatch inside = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch secondStarted = new CountDownLatch(1);
        AtomicBoolean first = new AtomicBoolean(true);
        JsoncMapper mapper = new JsoncMapper();
        mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override public Class<?>[] findViews(Annotated annotated) {
                if (first.compareAndSet(true, false)) {
                    inside.countDown();
                    try {
                        if (!release.await(10, TimeUnit.SECONDS)) { throw new AssertionError("release timed out"); }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new AssertionError(e);
                    }
                }
                return super.findViews(annotated);
            }
        });
        BeanDescription shared = mapper.getSerializationConfig().introspect(mapper.constructType(ViewedProbe.class));
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<Class<?>[]> a = pool.submit(shared::findDefaultViews);
            assertTrue(inside.await(10, TimeUnit.SECONDS));
            Future<Class<?>[]> b = pool.submit(() -> {
                secondStarted.countDown();
                return shared.findDefaultViews();
            });
            assertTrue(secondStarted.await(10, TimeUnit.SECONDS));
            // With the fix, B must wait for A. Without it, B returns null immediately.
            assertThrows(TimeoutException.class, () -> b.get(200, TimeUnit.MILLISECONDS));
            release.countDown();
            assertArrayEquals(new Class<?>[] { PublicView.class }, a.get(10, TimeUnit.SECONDS));
            assertArrayEquals(new Class<?>[] { PublicView.class }, b.get(10, TimeUnit.SECONDS));
        } finally {
            release.countDown();
            pool.shutdownNow();
            assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
        }
    }

    @Test
    void configuredMapperSupportsConcurrentJsoncRoundTrips() throws Exception {
        JsoncMapper mapper = new JsoncMapper();
        concurrently(() -> {
            for (int i = 0; i < 100; i++) {
                Probe value = mapper.readValue("{/*comment*/\"id\":\"x\",\"count\":7}", Probe.class);
                Probe copy = mapper.readValue(mapper.writeValueAsString(value), Probe.class);
                assertEquals("x", copy.id);
                assertEquals(7, copy.count);
            }
        });
    }
}
