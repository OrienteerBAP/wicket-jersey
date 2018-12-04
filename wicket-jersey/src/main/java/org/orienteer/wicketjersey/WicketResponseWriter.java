package org.orienteer.wicketjersey;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.wicket.request.http.WebResponse;
import org.glassfish.jersey.server.ContainerException;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WicketResponseWriter implements ContainerResponseWriter {
	
	private static final Logger LOG = LoggerFactory.getLogger(WicketResponseWriter.class);

	private WebResponse response;
	private final AtomicReference<TimeoutTimer> reference;
	private final ScheduledExecutorService scheduler;
	private final AtomicBoolean commited = new AtomicBoolean(false);
	
	public WicketResponseWriter(final WebResponse response, final ScheduledExecutorService scheduler) {
		this.response = response;
		this.reference = new AtomicReference<TimeoutTimer>();
		this.scheduler = scheduler;
	}

	@Override
	public OutputStream writeResponseStatusAndHeaders(long contentLength, ContainerResponse context)
			throws ContainerException {
        final javax.ws.rs.core.Response.StatusType statusInfo = context.getStatusInfo();

        final int code = statusInfo.getStatusCode();
        
        if(code / 100 == 2) {
        	response.setStatus(code);
        } else {
	        String reason = statusInfo.getReasonPhrase() == null
	                ? Status.fromStatusCode(code).getReasonPhrase()
	                : statusInfo.getReasonPhrase();
	        
	        response.sendError(code, reason);
        }

        if (contentLength != -1) {
            response.setContentLength(contentLength);
        }
        MediaType mediaType = context.getMediaType();
        if(mediaType!=null ) response.setContentType(mediaType.toString());
        for (final Map.Entry<String, List<String>> e : context.getStringHeaders().entrySet()) {
            for (final String value : e.getValue()) {
            	response.addHeader(e.getKey(), value);
            }
        }

        return response.getOutputStream();
	}

	@Override
	public boolean suspend(long timeOut, TimeUnit timeUnit, TimeoutHandler timeoutHandler) {
		try {
            TimeoutTimer timer = reference.get();

            if (timer == null) {
                TimeoutDispatcher task = new TimeoutDispatcher(this, timeoutHandler);
                ScheduledFuture<?> future =
                        scheduler.schedule(task, timeOut == 0 ? Integer.MAX_VALUE : timeOut,
                                timeOut == 0 ? TimeUnit.SECONDS : timeUnit);
                timer = new TimeoutTimer(scheduler, future, task);
                reference.set(timer);
                return true;
            }
            return false;
        } catch (final IllegalStateException ex) {
            return false;
        } finally {
            LOG.debug("suspend(...) called");
        }
	}

	@Override
	public void setSuspendTimeout(long timeOut, TimeUnit timeUnit) throws IllegalStateException {
        try {
            TimeoutTimer timer = reference.get();

            if (timer == null) {
                throw new IllegalStateException("Response has not been suspended");
            }
            timer.reschedule(timeOut, timeUnit);
        } finally {
            LOG.debug("setTimeout(...) called");
        }
	}

	@Override
	public void commit() {
		response.flush();
		response.close();
		commited.set(true);
	}

	@Override
	public void failure(Throwable error) {
		try {
            if (!commited.get()) {
            	response.setStatus(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            	response.getOutputStream().write(error.getMessage().getBytes());
            }
        } catch (IOException e) {
			throw new ContainerException("Can't provide message for error page", e);
		} finally {
            LOG.debug("failure(...) called");
            commit();
            rethrow(error);
        }
	}
	
	
    /**
     * Rethrow the original exception as required by JAX-RS, 3.3.4
     *
     * @param error throwable to be re-thrown
     */
    private void rethrow(final Throwable error) {
        if (error instanceof RuntimeException) {
            throw (RuntimeException) error;
        } else {
            throw new ContainerException(error);
        }
    }

	@Override
	public boolean enableResponseBuffering() {
		return false;
	}
	
	private static final class TimeoutTimer {

        private final AtomicReference<ScheduledFuture<?>> reference;
        private final ScheduledExecutorService service;
        private final TimeoutDispatcher task;

        public TimeoutTimer(ScheduledExecutorService service, ScheduledFuture<?> future,
                            TimeoutDispatcher task) {
            this.reference = new AtomicReference<ScheduledFuture<?>>();
            this.service = service;
            this.task = task;
        }

        public void reschedule(long timeOut, TimeUnit timeUnit) {
            ScheduledFuture<?> future = reference.getAndSet(null);

            if (future != null) {
                if (future.cancel(false)) {
                    future = service.schedule(task, timeOut == 0 ? Integer.MAX_VALUE : timeOut,
                            timeOut == 0 ? TimeUnit.SECONDS : timeUnit);
                    reference.set(future);
                }
            } else {
                future = service.schedule(task, timeOut == 0 ? Integer.MAX_VALUE : timeOut,
                        timeOut == 0 ? TimeUnit.SECONDS : timeUnit);
                reference.set(future);
            }
        }
    }
	
    static final class TimeoutDispatcher implements Runnable {

        private final WicketResponseWriter writer;
        private final TimeoutHandler handler;

        public TimeoutDispatcher(WicketResponseWriter writer, TimeoutHandler handler) {
            this.writer = writer;
            this.handler = handler;
        }

        public void run() {
            try {
                handler.onTimeout(writer);
            } catch (Exception e) {
                LOG.error("Failed to call timeout handler", e);
            }
        }
    }

}
