package org.moreservletapi.log;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @see org.springframework.web.util.ContentCachingRequestWrapper
 * @see org.springframework.http.server.ServletServerHttpRequest
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private final ByteArrayOutputStream body = new ByteArrayOutputStream();
    private final AtomicBoolean eof = new AtomicBoolean();

    private final Consumer<byte[]> notifyCallback;

    public RequestWrapper(HttpServletRequest request, Consumer<byte[]> notifyCallback) {
        super(request);
        this.notifyCallback = notifyCallback;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ServletInputStream delegate = super.getInputStream();
        return new DelegateServletInputStream(delegate) {

            @Override
            public int read() throws IOException {
                int ch = super.read();
                if (ch != -1) {
                    // note: no mark supported
                    body.write(ch);
                } else {
                    notifyEof();
                }
                return ch;
            }

            @Override
            public int read(byte[] bb) throws IOException {
                int n = super.read(bb);
                if (n != -1) {
                    // note: no mark supported
                    body.write(bb, 0, n);
                } else {
                    notifyEof();
                }
                return n;
            }

            @Override
            public int read(byte[] bb, int off, int len) throws IOException {
                int n = super.read(bb, off, len);
                if (n != -1) {
                    // note: no mark supported
                    body.write(bb, off, n);
                } else {
                    notifyEof();
                }
                return n;
            }

            @Override
            public void close() throws IOException {
                super.close();
                notifyEof();
            }

            @Override
            public boolean markSupported() {
                // в текущей реализации сервера вызывается в одном месте из недр spring webmvc
                // делает буферизованный InputStream само
                // пока не заморачиваемся
                return false;
            }
        };
    }

    @Override
    public String getParameter(String name) {
        notifyEofIfForm();
        return super.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        notifyEofIfForm();
        return super.getParameterMap();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        notifyEofIfForm();
        return super.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        notifyEofIfForm();
        return super.getParameterValues(name);
    }

    public void notifyEof() {
        if (!eof.compareAndSet(false, true)) {
            return;
        }

        byte[] bb = body.toByteArray();
        notifyCallback.accept(bb);
    }

    private void notifyEofIfForm() {
        if (body.size() == 0 && isFormPost()) {
            notifyEof();
        }
    }

    private boolean isFormPost() {
        String contentType = getContentType();
        return contentType != null
                && contentType.contains("application/x-www-form-urlencoded")
                && "POST".equals(getMethod());
    }
}
