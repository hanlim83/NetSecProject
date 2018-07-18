package Model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class ScheduledExecutorServiceHandler {
    private static ScheduledExecutorService service;
    private ScheduledFuture tableviewRunnable;
    private ScheduledFuture captureRunnable;
    private ScheduledFuture getSQLRunnable;
    private ScheduledFuture chartDataRunnable;

    public ScheduledExecutorServiceHandler() {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println(cores);
        service = Executors.newScheduledThreadPool(cores);
    }

    public static ScheduledExecutorService getService() {
        return service;
    }

    public void shutdownService() {
        if (tableviewRunnable != null)
            cancelTableviewRunnable();
        service.shutdown();
    }

    public void forceShutdownService() {
        service.shutdownNow();
    }

    public ScheduledFuture getTableviewRunnable() {
        return tableviewRunnable;
    }

    public void setTableviewRunnable(ScheduledFuture tableviewRunnable) {
        this.tableviewRunnable = tableviewRunnable;
    }

    public boolean getStatusTableviewRunnable() {
        return !tableviewRunnable.isDone();
    }

    public void cancelTableviewRunnable() {
        tableviewRunnable.cancel(true);
    }

    public ScheduledFuture getcaptureRunnable() {
        return captureRunnable;
    }

    public void setcaptureRunnable(ScheduledFuture captureRunnable) {
        this.captureRunnable = captureRunnable;
    }

    public boolean getStatuscaptureRunnable() {
        return !captureRunnable.isDone();
    }

    public void cancelcaptureRunnable() {
        captureRunnable.cancel(true);
    }

    public ScheduledFuture getgetSQLRunnable() {
        return getSQLRunnable;
    }

    public void setgetSQLRunnable(ScheduledFuture getSQLRunnable) {
        this.getSQLRunnable = getSQLRunnable;
    }

    public boolean getStatusgetSQLRunnable() {
        return !getSQLRunnable.isDone();
    }

    public void cancelgetSQLRunnable() {
        getSQLRunnable.cancel(true);
    }

    public ScheduledFuture getchartDataRunnable() {
        return chartDataRunnable;
    }

    public void setchartDataRunnable(ScheduledFuture chartDataRunnable) {
        this.chartDataRunnable = chartDataRunnable;
    }

    public boolean getStatuschartDataRunnable() {
        return !chartDataRunnable.isDone();
    }

    public void cancelchartDataRunnable() {
        chartDataRunnable.cancel(true);
    }
}
