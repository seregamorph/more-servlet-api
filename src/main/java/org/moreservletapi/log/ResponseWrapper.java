package org.moreservletapi.log;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream body = new ByteArrayOutputStream();

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        ServletOutputStream delegate = super.getOutputStream();
        return new DelegateServletOutputStream(delegate) {

            @Override
            public void write(int b) throws IOException {
                super.write(b);
                body.write(b);
            }

            @Override
            public void write(byte[] bb) throws IOException {
                super.write(bb);
                body.write(bb);
            }

            @Override
            public void write(byte[] bb, int off, int len) throws IOException {
                super.write(bb, off, len);
                body.write(bb, off, len);
            }
        };
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        PrintWriter delegate = super.getWriter();
        PrintWriter bodyWriter = new PrintWriter(new OutputStreamWriter(body, UTF_8), true);
        return new PrintWriter(delegate) {
            @Override
            public void write(int c) {
                super.write(c);
                bodyWriter.write(c);
            }

            @Override
            public void write(char[] buf) {
                super.write(buf);
                bodyWriter.write(buf);
            }

            @Override
            public void write(char[] buf, int off, int len) {
                super.write(buf, off, len);
                bodyWriter.write(buf, off, len);
            }

            @Override
            public void write(String s, int off, int len) {
                super.write(s, off, len);
                bodyWriter.write(s, off, len);
            }
        };
    }

    public byte[] toByteArray() {
        return body.toByteArray();
    }
}
