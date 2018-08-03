package Model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ExecutorServiceHandler {
    private static ScheduledThreadPoolExecutor service;
    private static ScheduledFuture tableviewRunnable;
    private static ScheduledFuture getSQLRunnable;
    private static ScheduledFuture chartDataRunnable;
    private static ScheduledFuture showDataRunnable;
    private static ScheduledFuture sendEmailRunnable;
    private static ScheduledFuture createTPSRunnable;

    public ExecutorServiceHandler() {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println(cores);
        service = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(cores);
        service.setRemoveOnCancelPolicy(true);
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
        ExecutorServiceHandler.tableviewRunnable = tableviewRunnable;
    }

    public boolean getStatusTableviewRunnable() {
        return !tableviewRunnable.isDone();
    }

    public void cancelTableviewRunnable() {
        tableviewRunnable.cancel(true);
    }

    public ScheduledFuture getgetSQLRunnable() {
        return getSQLRunnable;
    }

    public void setgetSQLRunnable(ScheduledFuture getSQLRunnable) {
        ExecutorServiceHandler.getSQLRunnable = getSQLRunnable;
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
        ExecutorServiceHandler.chartDataRunnable = chartDataRunnable;
    }

    public boolean getStatuschartDataRunnable() {
        return !chartDataRunnable.isDone();
    }

    public void cancelchartDataRunnable() {
        chartDataRunnable.cancel(true);
    }

    public ScheduledFuture getshowDataRunnable() {
        return showDataRunnable;
    }

    public void setshowDataRunnable(ScheduledFuture showDataRunnable) {
        ExecutorServiceHandler.showDataRunnable = showDataRunnable;
    }

    public boolean getStatusshowDataRunnable() {
        return !showDataRunnable.isDone();
    }

    public void cancelshowDataRunnable() {
        showDataRunnable.cancel(true);
    }

    public ScheduledFuture getsendEmailRunnable() {
        return sendEmailRunnable;
    }

    public void setsendEmailRunnable(ScheduledFuture sendEmailRunnable) {
        ExecutorServiceHandler.sendEmailRunnable = sendEmailRunnable;
    }

    public boolean getStatussendEmailRunnable() {
        return !sendEmailRunnable.isDone();
    }

    public void cancelsendEmailRunnable() {
        sendEmailRunnable.cancel(true);
    }

    public ScheduledFuture getcreateTPSRunnable() {
        return createTPSRunnable;
    }

    public void setcreateTPSRunnable(ScheduledFuture createTPSRunnable) {
        ExecutorServiceHandler.createTPSRunnable = createTPSRunnable;
    }

    public boolean getStatuscreateTPSRunnable() {
        return !createTPSRunnable.isDone();
    }

    public void cancelcreateTPSRunnable() {
        createTPSRunnable.cancel(true);
    }
}