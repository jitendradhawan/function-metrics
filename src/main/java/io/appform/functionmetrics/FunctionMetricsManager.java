package io.appform.functionmetrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Global metrics manager that needs to be initialized at start
 */
public class FunctionMetricsManager {
    private static final Logger log = LoggerFactory.getLogger(FunctionMetricsManager.class.getSimpleName());

    private static MetricRegistry registry;
    private static String prefix;
    private static boolean enableParameterCapture;

    private FunctionMetricsManager() {}

    public static void initialize(String packageName, MetricRegistry registry, boolean enableParameterCapture) {
        log.info("Functional Metric prefix: {}", packageName);
        FunctionMetricsManager.registry = registry;
        FunctionMetricsManager.prefix = packageName;
        FunctionMetricsManager.enableParameterCapture = enableParameterCapture;
    }

    public static Optional<Timer> timer(final TimerDomain domain, final FunctionInvocation invocation) {
        if(null == registry) {
            log.warn("Please call FunctionalMetricsManager.initialize() to setup metrics collection. No metrics will be pushed.");
            return Optional.empty();
        }
        if (!Strings.isNullOrEmpty(invocation.getParameterString()) && enableParameterCapture) {
            return Optional.of(registry.timer(
                    String.format("%s.%s.%s.%s.%s",
                            prefix,
                            invocation.getClassName(),
                            invocation.getMethodName(),
                            invocation.getParameterString(),
                            domain.getValue())));
        } else {
            return Optional.of(registry.timer(
                    String.format("%s.%s.%s.%s",
                            prefix,
                            invocation.getClassName(),
                            invocation.getMethodName(),
                            domain.getValue())));
        }
    }
}
