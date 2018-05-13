package org.moreservletapi.log;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;

class DelegateServletInputStream extends ServletInputStream {

    private final ServletInputStream delegate;

    DelegateServletInputStream(ServletInputStream delegate) {
        this.delegate = delegate;
    }

    @Override
    public int readLine(byte[] b, int off, int len) throws IOException {
        return delegate.readLine(b, off, len);
    }

    @Override
    public boolean isFinished() {
        return delegate.isFinished();
    }

    @Override
    public boolean isReady() {
        return delegate.isReady();
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        delegate.setReadListener(readListener);
    }

    @Override
    public int read() throws IOException {
        return delegate.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return delegate.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return delegate.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return delegate.skip(n);
    }

    @Override
    public int available() throws IOException {
        return delegate.available();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public void mark(int readlimit) {
        delegate.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        delegate.reset();
    }

    @Override
    public boolean markSupported() {
        return delegate.markSupported();
    }
}
