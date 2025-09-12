package com.rivemc.guilds;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A comprehensive wrapper around CompletableFuture that provides fluent error handling,
 * success callbacks, and all CompletableFuture utility methods with builder pattern support.
 *
 * @author Mqzen
 * @date 5/6/2025 (dd/MM/yyyy)
 */
public final class FutureOperation<T> {
    
    private final CompletableFuture<T> future;
    
    private FutureOperation(CompletableFuture<T> future) {
        this.future = future;
    }
    
    // ============= FACTORY METHODS =============
    
    /**
     * Create a FutureOperation from a CompletableFuture
     */
    public static <T> FutureOperation<T> of(CompletableFuture<T> future) {
        return new FutureOperation<>(future);
    }
    
    /**
     * Create a FutureOperation from a CompletionStage
     */
    public static <T> FutureOperation<T> of(CompletionStage<T> stage) {
        return new FutureOperation<>(stage.toCompletableFuture());
    }
    
    /**
     * Create a completed FutureOperation with a value
     */
    public static <T> FutureOperation<T> completed(T value) {
        return new FutureOperation<>(CompletableFuture.completedFuture(value));
    }
    
    /**
     * Create a failed FutureOperation with an exception
     */
    public static <T> FutureOperation<T> failed(Throwable exception) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(exception);
        return new FutureOperation<>(future);
    }
    
    /**
     * Create a FutureOperation that runs asynchronously
     */
    public static FutureOperation<Void> runAsync(Runnable runnable) {
        return new FutureOperation<>(CompletableFuture.runAsync(runnable));
    }
    
    /**
     * Create a FutureOperation that runs asynchronously with custom executor
     */
    public static FutureOperation<Void> runAsync(Runnable runnable, Executor executor) {
        return new FutureOperation<>(CompletableFuture.runAsync(runnable, executor));
    }
    
    /**
     * Create a FutureOperation that supplies a value asynchronously
     */
    public static <T> FutureOperation<T> supplyAsync(Supplier<T> supplier) {
        return new FutureOperation<>(CompletableFuture.supplyAsync(supplier));
    }
    
    /**
     * Create a FutureOperation that supplies a value asynchronously with custom executor
     */
    public static <T> FutureOperation<T> supplyAsync(Supplier<T> supplier, Executor executor) {
        return new FutureOperation<>(CompletableFuture.supplyAsync(supplier, executor));
    }
    
    // ============= COMPLETION HANDLING =============
    
    /**
     * Handle success with automatic error handling (printStackTrace)
     */
    public FutureOperation<T> onSuccess(Consumer<T> onSuccess) {
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            } else if (onSuccess != null) {
                onSuccess.accept(result);
            }
        });
        return this;
    }
    
    /**
     * Handle success for CompletableFuture<Void> with automatic error handling
     */
    public FutureOperation<T> onSuccess(Runnable onSuccess) {
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            } else if (onSuccess != null) {
                onSuccess.run();
            }
        });
        return this;
    }
    
    /**
     * Handle errors with custom error handler
     */
    public FutureOperation<T> onError(Consumer<Throwable> onError) {
        future.exceptionally(ex -> {
            if (ex != null && onError != null) {
                onError.accept(ex);
            }
            return null;
        });
        return this;
    }
    
    /**
     * Handle both success and error
     */
    public FutureOperation<T> handle(Consumer<T> onSuccess, Consumer<Throwable> onError) {
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                if (onError != null) {
                    onError.accept(ex);
                } else {
                    ex.printStackTrace();
                }
            } else if (onSuccess != null) {
                onSuccess.accept(result);
            }
        });
        return this;
    }
    
    /**
     * Handle both success and error for CompletableFuture<Void>
     */
    public FutureOperation<T> handle(Runnable onSuccess, Consumer<Throwable> onError) {
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                if (onError != null) {
                    onError.accept(ex);
                } else {
                    ex.printStackTrace();
                }
            } else if (onSuccess != null) {
                onSuccess.run();
            }
        });
        return this;
    }
    
    /**
     * Only handle errors with printStackTrace (no success callback)
     */
    public FutureOperation<T> printErrors() {
        return onError(Throwable::printStackTrace);
    }
    
    /**
     * Execute after completion (both success and error)
     */
    public FutureOperation<T> always(Runnable action) {
        future.whenComplete((result, ex) -> {
            if (action != null) {
                action.run();
            }
        });
        return this;
    }
    
    // ============= TRANSFORMATION METHODS =============
    
    /**
     * Transform the result and return a new FutureOperation
     */
    public <U> FutureOperation<U> map(Function<T, U> mapper) {
        return new FutureOperation<>(future.thenApply(mapper));
    }
    
    /**
     * Flat map to another CompletableFuture
     */
    public <U> FutureOperation<U> flatMap(Function<T, CompletableFuture<U>> mapper) {
        return new FutureOperation<>(future.thenCompose(mapper));
    }
    
    /**
     * Apply a function to the result and return a new FutureOperation
     */
    public <U> FutureOperation<U> thenApply(Function<T, U> function) {
        return new FutureOperation<>(future.thenApply(function));
    }
    
    /**
     * Apply a function to the result asynchronously
     */
    public <U> FutureOperation<U> thenApplyAsync(Function<T, U> function) {
        return new FutureOperation<>(future.thenApplyAsync(function));
    }
    
    /**
     * Apply a function to the result asynchronously with custom executor
     */
    public <U> FutureOperation<U> thenApplyAsync(Function<T, U> function, Executor executor) {
        return new FutureOperation<>(future.thenApplyAsync(function, executor));
    }
    
    /**
     * Consume the result without returning a value
     */
    public FutureOperation<Void> thenAccept(Consumer<T> action) {
        return new FutureOperation<>(future.thenAccept(action));
    }
    
    /**
     * Consume the result asynchronously without returning a value
     */
    public FutureOperation<Void> thenAcceptAsync(Consumer<T> action) {
        return new FutureOperation<>(future.thenAcceptAsync(action));
    }
    
    /**
     * Consume the result asynchronously with custom executor
     */
    public FutureOperation<Void> thenAcceptAsync(Consumer<T> action, Executor executor) {
        return new FutureOperation<>(future.thenAcceptAsync(action, executor));
    }
    
    /**
     * Execute a Runnable after successful completion
     */
    public FutureOperation<Void> thenRun(Runnable action) {
        return new FutureOperation<>(future.thenRun(action));
    }
    
    /**
     * Execute a Runnable asynchronously after successful completion
     */
    public FutureOperation<Void> thenRunAsync(Runnable action) {
        return new FutureOperation<>(future.thenRunAsync(action));
    }
    
    /**
     * Execute a Runnable asynchronously with custom executor
     */
    public FutureOperation<Void> thenRunAsync(Runnable action, Executor executor) {
        return new FutureOperation<>(future.thenRunAsync(action, executor));
    }
    
    /**
     * Compose with another CompletableFuture-returning function
     */
    public <U> FutureOperation<U> thenCompose(Function<T, CompletableFuture<U>> function) {
        return new FutureOperation<>(future.thenCompose(function));
    }
    
    /**
     * Compose asynchronously with another CompletableFuture-returning function
     */
    public <U> FutureOperation<U> thenComposeAsync(Function<T, CompletableFuture<U>> function) {
        return new FutureOperation<>(future.thenComposeAsync(function));
    }
    
    /**
     * Compose asynchronously with custom executor
     */
    public <U> FutureOperation<U> thenComposeAsync(Function<T, CompletableFuture<U>> function, Executor executor) {
        return new FutureOperation<>(future.thenComposeAsync(function, executor));
    }
    
    // ============= COMBINATION METHODS =============
    
    /**
     * Combine with another CompletableFuture using a BiFunction
     */
    public <U, V> FutureOperation<V> thenCombine(CompletableFuture<U> other, BiFunction<T, U, V> function) {
        return new FutureOperation<>(future.thenCombine(other, function));
    }
    
    /**
     * Combine with another FutureOperation using a BiFunction
     */
    public <U, V> FutureOperation<V> thenCombine(FutureOperation<U> other, BiFunction<T, U, V> function) {
        return new FutureOperation<>(future.thenCombine(other.unwrap(), function));
    }
    
    /**
     * Combine asynchronously with another CompletableFuture
     */
    public <U, V> FutureOperation<V> thenCombineAsync(CompletableFuture<U> other, BiFunction<T, U, V> function) {
        return new FutureOperation<>(future.thenCombineAsync(other, function));
    }
    
    /**
     * Combine asynchronously with custom executor
     */
    public <U, V> FutureOperation<V> thenCombineAsync(CompletableFuture<U> other, BiFunction<T, U, V> function, Executor executor) {
        return new FutureOperation<>(future.thenCombineAsync(other, function, executor));
    }
    
    /**
     * Accept both results when both complete
     */
    public <U> FutureOperation<Void> thenAcceptBoth(CompletableFuture<U> other, BiConsumer<T, U> action) {
        return new FutureOperation<>(future.thenAcceptBoth(other, action));
    }
    
    /**
     * Accept both results when both complete (with FutureOperation)
     */
    public <U> FutureOperation<Void> thenAcceptBoth(FutureOperation<U> other, BiConsumer<T, U> action) {
        return new FutureOperation<>(future.thenAcceptBoth(other.unwrap(), action));
    }
    
    /**
     * Run after both complete
     */
    public FutureOperation<Void> runAfterBoth(CompletableFuture<?> other, Runnable action) {
        return new FutureOperation<>(future.runAfterBoth(other, action));
    }
    
    /**
     * Run after both complete (with FutureOperation)
     */
    public FutureOperation<Void> runAfterBoth(FutureOperation<?> other, Runnable action) {
        return new FutureOperation<>(future.runAfterBoth(other.unwrap(), action));
    }
    
    // ============= EITHER METHODS =============
    
    /**
     * Apply function when either this or other completes
     */
    public <U> FutureOperation<U> applyToEither(CompletableFuture<T> other, Function<T, U> function) {
        return new FutureOperation<>(future.applyToEither(other, function));
    }
    
    /**
     * Apply function when either this or other completes (with FutureOperation)
     */
    public <U> FutureOperation<U> applyToEither(FutureOperation<T> other, Function<T, U> function) {
        return new FutureOperation<>(future.applyToEither(other.unwrap(), function));
    }
    
    /**
     * Accept when either this or other completes
     */
    public FutureOperation<Void> acceptEither(CompletableFuture<T> other, Consumer<T> action) {
        return new FutureOperation<>(future.acceptEither(other, action));
    }
    
    /**
     * Accept when either this or other completes (with FutureOperation)
     */
    public FutureOperation<Void> acceptEither(FutureOperation<T> other, Consumer<T> action) {
        return new FutureOperation<>(future.acceptEither(other.unwrap(), action));
    }
    
    /**
     * Run after either this or other completes
     */
    public FutureOperation<Void> runAfterEither(CompletableFuture<?> other, Runnable action) {
        return new FutureOperation<>(future.runAfterEither(other, action));
    }
    
    /**
     * Run after either this or other completes (with FutureOperation)
     */
    public FutureOperation<Void> runAfterEither(FutureOperation<?> other, Runnable action) {
        return new FutureOperation<>(future.runAfterEither(other.unwrap(), action));
    }
    
    // ============= EXCEPTION HANDLING =============
    
    /**
     * Handle exceptions and provide a fallback value
     */
    public FutureOperation<T> exceptionally(Function<Throwable, T> function) {
        return new FutureOperation<>(future.exceptionally(function));
    }
    
    /**
     * Handle completion with custom handler (can handle both success and failure)
     */
    public <U> FutureOperation<U> handleCompletion(BiFunction<T, Throwable, U> function) {
        return new FutureOperation<>(future.handle(function));
    }
    
    /**
     * Handle completion asynchronously
     */
    public <U> FutureOperation<U> handleAsync(BiFunction<T, Throwable, U> function) {
        return new FutureOperation<>(future.handleAsync(function));
    }
    
    /**
     * Handle completion asynchronously with custom executor
     */
    public <U> FutureOperation<U> handleAsync(BiFunction<T, Throwable, U> function, Executor executor) {
        return new FutureOperation<>(future.handleAsync(function, executor));
    }
    
    /**
     * Execute action when complete (both success and error)
     */
    public FutureOperation<T> whenComplete(BiConsumer<T, Throwable> action) {
        return new FutureOperation<>(future.whenComplete(action));
    }
    
    /**
     * Execute action asynchronously when complete
     */
    public FutureOperation<T> whenCompleteAsync(BiConsumer<T, Throwable> action) {
        return new FutureOperation<>(future.whenCompleteAsync(action));
    }
    
    /**
     * Execute action asynchronously with custom executor when complete
     */
    public FutureOperation<T> whenCompleteAsync(BiConsumer<T, Throwable> action, Executor executor) {
        return new FutureOperation<>(future.whenCompleteAsync(action, executor));
    }
    
    // ============= UTILITY METHODS =============
    
    /**
     * Get the underlying CompletableFuture if needed
     */
    public CompletableFuture<T> unwrap() {
        return future;
    }
    
    /**
     * Convert to CompletionStage
     */
    public CompletionStage<T> toCompletionStage() {
        return future;
    }
    
    /**
     * Get the result (blocking)
     */
    public T join() {
        return future.join();
    }
    
    /**
     * Get the result with timeout
     */
    public T get(long timeout, TimeUnit unit) throws Exception {
        return future.get(timeout, unit);
    }
    
    /**
     * Get the result immediately if available
     */
    public T getNow(T valueIfAbsent) {
        return future.getNow(valueIfAbsent);
    }
    
    /**
     * Check if the future is done
     */
    public boolean isDone() {
        return future.isDone();
    }
    
    /**
     * Check if the future was cancelled
     */
    public boolean isCancelled() {
        return future.isCancelled();
    }
    
    /**
     * Check if the future completed exceptionally
     */
    public boolean isCompletedExceptionally() {
        return future.isCompletedExceptionally();
    }
    
    /**
     * Cancel the future
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }
    
    /**
     * Complete the future with a value
     */
    public boolean complete(T value) {
        return future.complete(value);
    }
    
    /**
     * Complete the future exceptionally
     */
    public boolean completeExceptionally(Throwable ex) {
        return future.completeExceptionally(ex);
    }
    
    // ============= STATIC UTILITY METHODS =============
    
    /**
     * Combine multiple FutureOperations into one that completes when all complete
     */
    @SafeVarargs
    public static FutureOperation<Void> allOf(FutureOperation<?>... futures) {
        CompletableFuture<?>[] completableFutures = Arrays.stream(futures)
            .map(FutureOperation::unwrap)
            .toArray(CompletableFuture[]::new);
        return new FutureOperation<>(CompletableFuture.allOf(completableFutures));
    }
    
    /**
     * Combine multiple CompletableFutures into one FutureOperation
     */
    public static FutureOperation<Void> allOf(CompletableFuture<?>... futures) {
        return new FutureOperation<>(CompletableFuture.allOf(futures));
    }
    
    /**
     * Combine list of FutureOperations
     */
    public static FutureOperation<Void> allOf(List<FutureOperation<?>> futures) {
        CompletableFuture<?>[] completableFutures = futures.stream()
            .map(FutureOperation::unwrap)
            .toArray(CompletableFuture[]::new);
        return new FutureOperation<>(CompletableFuture.allOf(completableFutures));
    }
    
    /**
     * Create a FutureOperation that completes when any of the given futures complete
     */
    @SafeVarargs
    public static FutureOperation<Object> anyOf(FutureOperation<?>... futures) {
        CompletableFuture<?>[] completableFutures = Arrays.stream(futures)
            .map(FutureOperation::unwrap)
            .toArray(CompletableFuture[]::new);
        return new FutureOperation<>(CompletableFuture.anyOf(completableFutures));
    }
    
    /**
     * Create a FutureOperation that completes when any of the given CompletableFutures complete
     */
    public static FutureOperation<Object> anyOf(CompletableFuture<?>... futures) {
        return new FutureOperation<>(CompletableFuture.anyOf(futures));
    }
    
    /**
     * Create a FutureOperation with a delay
     */
    public static FutureOperation<Void> delay(long delay, TimeUnit unit) {
        CompletableFuture<Void> delayed = new CompletableFuture<>();
        // Note: In a real implementation, you'd use a ScheduledExecutorService
        // This is a simplified version
        new Thread(() -> {
            try {
                Thread.sleep(unit.toMillis(delay));
                delayed.complete(null);
            } catch (InterruptedException e) {
                delayed.completeExceptionally(e);
                Thread.currentThread().interrupt();
            }
        }).start();
        return new FutureOperation<>(delayed);
    }
}