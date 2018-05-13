package org.moreservletapi.log;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class RequestWrapperTest {

    @Test
    public void testInputStream() throws IOException {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/test");
        byte[] expectedBytes = ("" +
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<request>\n" +
                "    <auth>\n" +
                "        <id>WL-1000000000006752</id>\n" +
                "        <auth_type>TOKEN</auth_type>\n" +
                "        <key>***</key>\n" +
                "        <client_soft>test</client_soft>\n" +
                "    </auth>\n" +
                "    <request_type>test</request_type>\n" +
                "    <from prv_id=\"2040\"/>\n" +
                "    <to cur_id=\"643\" amount=\"10\"/>\n" +
                "</request>\n").getBytes(UTF_8);
        req.setContent(expectedBytes);

        AtomicReference<byte[]> actualBytes1 = new AtomicReference<>();
        RequestWrapper requestWrapper = new RequestWrapper(req, actualBytes1::set);

        try (InputStream in = requestWrapper.getInputStream()) {
            assertThat(in)
                    .hasSameContentAs(new ByteArrayInputStream(expectedBytes));
        }
        assertThat(actualBytes1.get())
                .isEqualTo(expectedBytes);
    }
}
