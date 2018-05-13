package org.moreservletapi.log;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LogEscapeUtilsTest {

    @Test
    public void testEscapeXml() {
        String xmlTabs = "" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<request>\n" +
                "\t<auth>\n" +
                "\t\t<id>login</id>\n" +
                "\t\t<auth_type>PASSWORD</auth_type>\n" +
                "\t\t<client_soft>soft</client_soft>\n" +
                "\t\t<key>password</key>\n" +
                "\t\t<timestamp>2016-01-02T12:34:56.123Z</timestamp>\n" +
                "\t</auth>\n" +
                "\t<body uid=\"uid123\" comment=\"комментарий\" livetime=\"100\">\n" +
                "\t\t<from provider=\"12345\" account=\"from_account\" currency=\"643\" amount=\"100.25\"/>\n" +
                "\t\t<to provider=\"123\" account=\"to_account\" currency=\"643\" amount=\"100.25\" />\n" +
                "\t\t<success_url>http://example.com</success_url>\n" +
                "\t\t<fail_url>http://example1.com</fail_url>\n" +
                "\t\t<extras>\n" +
                "\t\t\t<extra_name1>extra_value1</extra_name1>\n" +
                "\t\t\t<extra_name2>extra_value2</extra_name2>\n" +
                "\t\t</extras>\n" +
                "\t</body>\n" +
                "\t<client>\n" +
                "\t\t<email>email@email.com</email>\n" +
                "\t</client>\n" +
                "</request>";
        String xmlSpaces = "" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<request>\n" +
                "    <auth>\n" +
                "        <id>login</id>\n" +
                "        <auth_type>PASSWORD</auth_type>\n" +
                "        <client_soft>soft</client_soft>\n" +
                "        <key>password</key>\n" +
                "        <timestamp>2016-01-02T12:34:56.123Z</timestamp>\n" +
                "    </auth>\n" +
                "    <body uid=\"uid123\" comment=\"комментарий\" livetime=\"100\">\n" +
                "        <from provider=\"12345\" account=\"from_account\" currency=\"643\" amount=\"100.25\"/>\n" +
                "        <to provider=\"123\" account=\"to_account\" currency=\"643\" amount=\"100.25\" />\n" +
                "        <success_url>http://example.com</success_url>\n" +
                "        <fail_url>http://example1.com</fail_url>\n" +
                "        <extras>\n" +
                "            <extra_name1>extra_value1</extra_name1>\n" +
                "            <extra_name2>extra_value2</extra_name2>\n" +
                "        </extras>\n" +
                "    </body>\n" +
                "    <client>\n" +
                "        <email>email@email.com</email>\n" +
                "    </client>\n" +
                "</request>";
        assertThat(LogEscapeUtils.escapeWithLf(xmlTabs))
                .isEqualTo(xmlSpaces);
        assertThat(LogEscapeUtils.escapeWithLf("\t\u0000" + xmlSpaces))
                .isEqualTo("    %00" + xmlSpaces);
    }

    @Test
    public void testEscapeJson() {
        String json = "" +
                "{\n" +
                "  \"auth\": {\n" +
                "    \"id\": \"login\",\n" +
                "    \"auth_type\": \"PASSWORD\",\n" +
                "    \"client_soft\": \"soft\",\n" +
                "    \"key\": \"password\",\n" +
                "    \"timestamp\": \"2016-01-02T12:34:56.123Z\"\n" +
                "  },\n" +
                "  \"body\": {\n" +
                "    \"uid\": \"uid123\",\n" +
                "    \"comment\": \"комментарий\",\n" +
                "    \"livetime\": 100,\n" +
                "    \"from\": {\n" +
                "      \"provider\": 12345,\n" +
                "      \"account\": \"from_account\",\n" +
                "      \"currency\": 643,\n" +
                "      \"amount\": \"100.25\"\n" +
                "    },\n" +
                "    \"to\": {\n" +
                "      \"provider\": 123,\n" +
                "      \"account\": \"to_account\",\n" +
                "      \"currency\": 643,\n" +
                "      \"amount\": 100.25\n" +
                "    },\n" +
                "    \"success_url\": \"http://example.com\",\n" +
                "    \"fail_url\": \"http://example1.com\",\n" +
                "    \"extras\": {\n" +
                "      \"extra_name1\": \"extra_value1\",\n" +
                "      \"extra_name2\": \"extra_value2\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"client\": {\n" +
                "    \"email\": \"email@email.com\"\n" +
                "  }\n" +
                "}";
        assertThat(LogEscapeUtils.escapeWithLf(json))
                .isEqualTo(json);
    }

    @Test
    public void testEscapeXml2() {
        String message = "" +
                "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<response>\n" +
                "    <result code=\"0\"/>\n" +
                "    <body id=\"111163\" body_uid=\"rere1010156\" body_date_utc=\"2018-02-01T09:01:19.938Z\" status=\"waiting\" error_code=\"0\"/>\n" +
                "    <redirect_url>https://terminal.test.com/#/wallet/invoice/1234567</redirect_url>\n" +
                "</response>";
        assertThat(LogEscapeUtils.escapeWithLf(message))
                .isEqualTo(message);
    }

    @Test
    public void testEscapeBadCharacters() {
        String message = "Message with \u0000-bytes\r\n" +
                "And also with \u0399 known as IOTA\r\n" +
                "Wanna some $?!";
        // note: no '\r' symbol
        String expected = "Message with %00-bytes\n" +
                "And also with %CE%99 known as IOTA\n" +
                "Wanna some $?!";
        assertThat(LogEscapeUtils.escapeWithLf(message))
                .isEqualTo(expected);
    }
}
