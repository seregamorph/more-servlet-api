package org.moreservletapi.log;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class ResponseWrapperTest {

    @Test
    public void testOutputStream() throws IOException {
        MockHttpServletResponse resp = new MockHttpServletResponse();
        byte[] expectedBytes = ("" +
                "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<response>\n" +
                "    <result code=\"0\"/>\n" +
                "    <body id=\"123\" body_uid=\"uid123\" date=\"2016-10-31T19:56:59.005Z\" error_code=\"247\" error_message=\"Invalid amount\" status=\"error\">\n" +
                "        <extra name=\"account\">1234567890</extra>\n" +
                "        <extra name=\"card_masked\">555555******4444</extra>\n" +
                "    </body>\n" +
                "</response>\n").getBytes(UTF_8);

        ResponseWrapper responseWrapper = new ResponseWrapper(resp);

        try (OutputStream in = responseWrapper.getOutputStream()) {
            in.write(expectedBytes);
        }

        assertThat(responseWrapper.toByteArray())
                .isEqualTo(expectedBytes);
    }
}
