package Model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ScheduledThreadPoolExecutorHandler {
    private static ScheduledThreadPoolExecutor service;
    private static ScheduledFuture tableviewRunnable;
    private static ScheduledFuture chartDataRunnable;
    private static ScheduledFuture backgroundRunnable;

    public ScheduledThreadPoolExecutorHandler() {
        if (service == null) {
            int cores = Runtime.getRuntime().availableProcessors();
            System.out.println(cores);
            service = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(cores);
            service.setRemoveOnCancelPolicy(true);
        } else {
            System.err.println("Service already Instantiated, will use existing service");
        }
    }

    public static ScheduledExecutorService getService() {
        return service;
    }

    public void shutdownService() {
        service.shutdown();
    }

    public void forceShutdownService() {
        service.shutdownNow();
    }

    public ScheduledFuture getTableviewRunnable() {
        return tableviewRunnable;
    }

    public void setTableviewRunnable(ScheduledFuture tableviewRunnable) {
        ScheduledThreadPoolExecutorHandler.tableviewRunnable = tableviewRunnable;
    }

    public boolean getStatusTableviewRunnable() {
        if (tableviewRunnable != null)
            return !tableviewRunnable.isDone();
        else {
            return false;
        }
    }

    public void cancelTableviewRunnable() {
        tableviewRunnable.cancel(true);
    }

    public ScheduledFuture getchartDataRunnable() {
        return chartDataRunnable;
    }

    public void setchartDataRunnable(ScheduledFuture chartDataRunnable) {
        ScheduledThreadPoolExecutorHandler.chartDataRunnable = chartDataRunnable;
    }

    public boolean getStatuschartDataRunnable() {
        if (chartDataRunnable != null)
            return !chartDataRunnable.isDone();
        else {
            return false;
        }
    }

    public void cancelchartDataRunnable() {
        chartDataRunnable.cancel(true);
    }

    public ScheduledFuture getbackgroundRunnable() {
        return backgroundRunnable;
    }

    public void setbackgroundRunnable(ScheduledFuture backgroundRunnable) {
        ScheduledThreadPoolExecutorHandler.backgroundRunnable = backgroundRunnable;
    }

    public boolean getStatusbackgroundRunnable() {
        if (backgroundRunnable != null)
            return !backgroundRunnable.isDone();
        else {
            return false;
        }
    }

    public void cancelbackgroundRunnable() {
        backgroundRunnable.cancel(true);
    }
}
