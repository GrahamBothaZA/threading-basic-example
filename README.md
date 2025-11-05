# Java Threading Principles

> **Safe, performant, and maintainable concurrency in Java**  
> Follow these principles to avoid race conditions, deadlocks, and resource leaks.

---

## 1. Prefer High-Level Concurrency Utilities

**Never use raw `Thread` or `Runnable` directly.**

```java
// Good
ExecutorService exec = Executors.newFixedThreadPool(4);

// Avoid
new Thread(runnable).start();
```

Benefits:

- Thread pooling
- Graceful shutdown
- Built-in error handling
- Resource management

## 2. Avoid Shared Mutable State
**Minimize shared data. Prefer immutability.**

```java
// Bad — race condition
int counter = 0;
exec.submit(() -> counter++);

// Good
AtomicInteger counter = new AtomicInteger();
exec.submit(counter::incrementAndGet);

// Best — immutable
record Result(int value) {}
```

## 3. Always Handle InterruptedException Properly
**Never swallow interrupts.**

```java
// Wrong
try { Thread.sleep(1000); } catch (InterruptedException e) { }

// Correct
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
    return; // or break
}
```

## 4. Use volatile or Atomic for Simple Flags

```java
volatile boolean running = true;
// or
AtomicBoolean active = new AtomicBoolean(true);
```
Use only for:

- One writer, many readers
- Simple start/stop flags

Not safe for: `i++, if (!flag) flag = true`

## 5. Use Proper Synchronization for Complex State

```java
private final Object lock = new Object();
private int count = 0;

void increment() {
synchronized(lock) { count++; }
}
```

Prefer `java.util.concurrent:`

- `ConcurrentHashMap`
- `BlockingQueue`
- `CountDownLatch, Semaphore, CyclicBarrier`

## 6. Never Call `Thread.stop()`

```java
thread.stop(); // DANGEROUS — corrupts state
```

```java
thread.interrupt();
```

```java
while (!Thread.currentThread().isInterrupted()) {
    // do work
}
```

## 7. Always Shut Down Executors

```java
ExecutorService exec = Executors.newFixedThreadPool(2);
try {
    // submit tasks
} finally {
    exec.shutdown();
    exec.awaitTermination(10, TimeUnit.SECONDS);
    // or exec.shutdownNow();
}
```

## 8. Keep Tasks Small and Independent
Each task should:

- Do one thing
- Have no side effects
- Be stateless or use local state

```java
exec.submit(() -> processFile("data.txt")); // Good
```

## 9. Use Timeouts When Waiting

```java
Future<?> f = exec.submit(task);
try {
    f.get(5, TimeUnit.SECONDS);
} catch (TimeoutException e) {
    f.cancel(true);
}
```
Prevents deadlocks and hung threads.

## 10. Handle Exceptions in Tasks

```java
exec.submit(() -> {
    try {
        // risky code
    } catch (Exception e) {
        log.error("Task failed", e);
    }
});
```

Or with `CompletableFuture`:

```java
future.exceptionally(ex -> { log.error("Failed", ex); return null; });
```

## 11. Avoid synchronized(this) — Use Private Locks

```java
// Bad
public synchronized void method() { ... }

// Good
private final Object lock = new Object();
public void method() {
    synchronized(lock) { ... }
}
```

## 12. Use Thread Pools Wisely

| **Pool Type** | **Use Case** |
|---------------|--------------|
| `newFixedThreadPool(n)`| Known number of workers|
| `newCachedThreadPool()`| Short-lived tasks|
| `newSingleThreadExecutor()`| Sequential execution|
| `newScheduledThreadPool()`| Periodic tasks|

## 13. Design for Testing
- Inject `ExecutorService`
- Use `CountDownLatch` in tests
- Avoid `Thread.sleep()` in production

## 14. Follow the “One Writer” Rule

`Only one thread should modify a piece of data.`

All others read or use thread-safe structures.

## 15. Document Threading Assumptions

```java
/**
* Thread-safe: uses ConcurrentHashMap
* Must be called from worker thread
* Do not modify input list after submission
*/
void submitTask(List<Task> tasks);
```

# The 5 Golden Rules
| **Rule** | **Why** |
|---------------|--------------|
|1. Use `ExecutorService`| No raw threads|
|2. Avoid shared mutable state| Prevent race conditions|
|3. Respect `interrupt()`| Graceful shutdown|
|4. Use `volatile/Atomic/synchronized` correctly| Visibility + atomicity|
|5. Always clean up| No resource leaks|

# Pre-Flight Checklist
- [ ] Using `ExecutorService`?
- [ ] No shared mutable state?
- [ ] Interrupts handled?
- [ ] Tasks check `isInterrupted()`?
- [ ] `shutdown()` called?
- [ ] Timeouts in place?

# Recommended Safe Pattern

```java
ExecutorService exec = Executors.newFixedThreadPool(4);
AtomicBoolean running = new AtomicBoolean(true);

exec.submit(() -> {
    while (running.get() && !Thread.currentThread().isInterrupted()) {
        try {
            // do work
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
    }
});

// Later:
running.set(false);
exec.shutdownNow();
```