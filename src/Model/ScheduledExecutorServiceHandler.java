package Model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class ScheduledExecutorServiceHandler {
    private static ScheduledExecutorService service;
    private ScheduledFuture tableviewRunnable;
    private ScheduledFuture NcaptureRunnable;
    private ScheduledFuture updateStatsFuture;
    private ScheduledFuture CcaptureRunnable;

    public ScheduledExecutorServiceHandler () {
        int cores = Runtime.getRuntime().availableProcessors();
        this.service = Executors.newScheduledThreadPool(cores);
    }

    public void shutdownService () {
        if (tableviewRunnable != null)
            cancelTableviewRunnable();
        if (NcaptureRunnable != null)
            cancelNcaptureRunnable();
        if (updateStatsFuture != null)
            cancelUpdateStatsFuture();
        if(CcaptureRunnable != null)
            cancelCcaptureRunnable();
        service.shutdown();
    }

    public void forceShutdownService () {
        service.shutdownNow();
    }

    public ScheduledFuture getTableviewRunnable() {
        return tableviewRunnable;
    }

    public static ScheduledExecutorService getService() {
        return service;
    }

    public void setTableviewRunnable(ScheduledFuture tableviewRunnable) {
        this.tableviewRunnable = tableviewRunnable;
    }

    public boolean getStatusTableviewRunnable () {
        return !tableviewRunnable.isDone();
    }

    public void cancelTableviewRunnable () {
        tableviewRunnable.cancel(true);
    }

    public ScheduledFuture getNcaptureRunnable() {
        return NcaptureRunnable;
    }

    public void setNcaptureRunnable(ScheduledFuture ncaptureRunnable) {
        NcaptureRunnable = ncaptureRunnable;
    }

    public boolean getStatusNcaptureRunnable () {
        return !NcaptureRunnable.isDone();
    }

    public void cancelNcaptureRunnable () {
        NcaptureRunnable.cancel(true);
    }

    public ScheduledFuture getUpdateStatsFuture() {
        return updateStatsFuture;
    }

    public void setUpdateStatsFuture(ScheduledFuture updateStatsFuture) {
        this.updateStatsFuture = updateStatsFuture;
    }

    public boolean getStatusUpdateStatsFuture () {
        return !updateStatsFuture.isDone();
    }

    public void cancelUpdateStatsFuture () {
        updateStatsFuture.cancel(true);
    }

    public ScheduledFuture getCcaptureRunnable() {
        return CcaptureRunnable;
    }

    public void setCcaptureRunnable(ScheduledFuture ccaptureRunnable) {
        CcaptureRunnable = ccaptureRunnable;
    }

    public boolean getStatusCcaptureRunnable () {
        return !CcaptureRunnable.isDone();
    }

    public void cancelCcaptureRunnable () {
        CcaptureRunnable.cancel(true);
    }
}
