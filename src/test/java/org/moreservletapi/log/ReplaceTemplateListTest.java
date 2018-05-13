package org.moreservletapi.log;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.moreservletapi.log.ReplaceTemplates.*;

public class ReplaceTemplateListTest {

    private final ReplaceTemplateList templateList = new ReplaceTemplateList()
            .add(ReplaceTemplates.AUTHORIZATION_BASIC)
            .add("<key>\\S+?</key>", "<key>***</key>")
            .add("<series>\\d+</series>", "<series>***</series>")
            .add("<number>\\d+</number>", "<number>***</number>")
            .add("<issued_date>[\\d\\-]+</issued_date>", "<issued_date>***</issued_date>")
            .add("\"key\"(\\s*):(\\s*)\"\\S+?\"", "\"key\"$1:$2\"***\"")
            .add("\"series\"(\\s*):(\\s*)\"\\d+\"", "\"series\"$1:$2\"***\"")
            .add("\"number\"(\\s*):(\\s*)\"\\d+\"", "\"number\"$1:$2\"***\"")
            .add("\"issued_date\"(\\s*):(\\s*)\"[\\d\\-]+\"", "\"issued_date\"$1:$2\"***\"");

    private final ReplaceTemplateList templateList2 = new ReplaceTemplateList()
            .add(ReplaceTemplates.AUTHORIZATION_BASIC)
            .add(maskXmlTag("key"))
            .add(maskXmlTag("series"))
            .add(maskXmlTag("number"))
            .add(maskXmlTag("issued_date"))
            .add(maskJsonField("key"))
            .add(maskJsonField("series"))
            .add(maskJsonField("number", "[\\da-zA-Zа-яА-Я\\s\\-\\.]+"))
            .add(maskJsonField("issued_date"));

    @Test
    public void test1() {
        ReplaceTemplate template = new ReplaceTemplate(Pattern.compile("([a-z]+)=(\\d+)"), "$2=$1");
        assertEquals(template.apply("abc=123 def=456 ghi=789"), "123=abc 456=def 789=ghi");
    }

    @Test
    public void testXml0() {
        testXmlImpl0(templateList);
        testXmlImpl0(templateList2);
    }

    private static void testXmlImpl0(ReplaceTemplateList replaceTemplateList) {
        String filteredBody = replaceTemplateList.apply("" +
                "<request>" +
                "<auth><id>id1</id><auth_type>PASSWORD</auth_type><client_soft>soft</client_soft><key>password</key><timestamp>2016-01-02T12:34:56.123Z</timestamp></auth>" +
                "<body uid=\"uid123\" comment=\"комментарий\">" +
                "<from provider=\"12345\" account=\"from_account\" currency=\"643\" amount=\"100.25\"/>" +
                "<to provider=\"123\" account=\"to_account\" currency=\"643\" amount=\"100.25\"/>" +
                "<success_url>http://example.com</success_url>" +
                "<fail_url>http://example1.com</fail_url>" +
                "<extras><extra_name1>extra_value1</extra_name1><extra_name2>extra_value2</extra_name2></extras>" +
                "</body>" +
                "<client></client>" +
                "</request>");
        assertEquals(filteredBody, "" +
                "<request>" +
                "<auth><id>id1</id><auth_type>PASSWORD</auth_type><client_soft>soft</client_soft><key>***</key><timestamp>2016-01-02T12:34:56.123Z</timestamp></auth>" +
                "<body uid=\"uid123\" comment=\"комментарий\">" +
                "<from provider=\"12345\" account=\"from_account\" currency=\"643\" amount=\"100.25\"/>" +
                "<to provider=\"123\" account=\"to_account\" currency=\"643\" amount=\"100.25\"/>" +
                "<success_url>http://example.com</success_url>" +
                "<fail_url>http://example1.com</fail_url>" +
                "<extras><extra_name1>extra_value1</extra_name1><extra_name2>extra_value2</extra_name2></extras>" +
                "</body>" +
                "<client></client>" +
                "</request>");
    }

    @Test
    public void testXml() {
        testXmlImpl(templateList);
        testXmlImpl(templateList2);
    }

    private static void testXmlImpl(ReplaceTemplateList replaceTemplateList) {
        String filteredBody = replaceTemplateList.apply("" +
                "Authorization: Basic a+b/=\n" +
                "\n" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<request>\n" +
                "    <auth>\n" +
                "        <id>id1</id>\n" +
                "        <auth_type>PASSWORD</auth_type>\n" +
                "        <client_soft>soft</client_soft>\n" +
                "        <key>password</key>\n" +
                "        <timestamp>2016-01-02T12:34:56.123Z</timestamp>\n" +
                "    </auth>\n" +
                "    <body uid=\"uid123\" comment=\"комментарий\">\n" +
                "        <from provider=\"12345\" account=\"from_account\" currency=\"643\" amount=\"100.25\"/>\n" +
                "        <to provider=\"123\" account=\"to_account\" currency=\"643\" amount=\"100.25\"/>\n" +
                "        <success_url>http://example.com</success_url>\n" +
                "        <fail_url>http://example1.com</fail_url>\n" +
                "        <extras>\n" +
                "            <extra_name1>extra_value1</extra_name1>\n" +
                "            <extra_name2>extra_value2</extra_name2>\n" +
                "        </extras>\n" +
                "    </body>\n" +
                "    <client>\n" +
                "    </client>\n" +
                "</request>\n");
        assertEquals(filteredBody, "" +
                "Authorization: Basic ***\n" +
                "\n" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<request>\n" +
                "    <auth>\n" +
                "        <id>id1</id>\n" +
                "        <auth_type>PASSWORD</auth_type>\n" +
                "        <client_soft>soft</client_soft>\n" +
                "        <key>***</key>\n" +
                "        <timestamp>2016-01-02T12:34:56.123Z</timestamp>\n" +
                "    </auth>\n" +
                "    <body uid=\"uid123\" comment=\"комментарий\">\n" +
                "        <from provider=\"12345\" account=\"from_account\" currency=\"643\" amount=\"100.25\"/>\n" +
                "        <to provider=\"123\" account=\"to_account\" currency=\"643\" amount=\"100.25\"/>\n" +
                "        <success_url>http://example.com</success_url>\n" +
                "        <fail_url>http://example1.com</fail_url>\n" +
                "        <extras>\n" +
                "            <extra_name1>extra_value1</extra_name1>\n" +
                "            <extra_name2>extra_value2</extra_name2>\n" +
                "        </extras>\n" +
                "    </body>\n" +
                "    <client>\n" +
                "    </client>\n" +
                "</request>\n");
        assertThat(filteredBody).doesNotContain("password");
    }

    @Test
    public void testJson0() {
        testJsonImpl0(templateList);
        testJsonImpl0(templateList2);
    }

    private static void testJsonImpl0(ReplaceTemplateList replaceTemplateList) {
        String filteredBody = replaceTemplateList.apply("" +
                "{\"auth\":{\"id\":\"login\",\"auth_type\":\"PASSWORD\",\"client_soft\":\"soft\",\"key\":\"password\",\"timestamp\":\"2016-01-02T12:34:56.123Z\"}," +
                "\"body\":{\"uid\":\"uid123\",\"comment\":\"комментарий\"," +
                "\"from\":{\"provider\":12345,\"account\":\"from_account\",\"currency\":643,\"amount\":\"100.25\"}," +
                "\"to\":{\"provider\":123,\"account\":\"to_account\",\"currency\":643,\"amount\":100.25}," +
                "\"extras\":{\"extra_name1\":\"extra_value1\",\"extra_name2\":\"extra_value2\"}}," +
                "\"client\":{}}");
        assertEquals(filteredBody, "" +
                "{\"auth\":{\"id\":\"login\",\"auth_type\":\"PASSWORD\",\"client_soft\":\"soft\",\"key\":\"***\",\"timestamp\":\"2016-01-02T12:34:56.123Z\"}," +
                "\"body\":{\"uid\":\"uid123\",\"comment\":\"комментарий\"," +
                "\"from\":{\"provider\":12345,\"account\":\"from_account\",\"currency\":643,\"amount\":\"100.25\"}," +
                "\"to\":{\"provider\":123,\"account\":\"to_account\",\"currency\":643,\"amount\":100.25}," +
                "\"extras\":{\"extra_name1\":\"extra_value1\",\"extra_name2\":\"extra_value2\"}}," +
                "\"client\":{}}");
    }

    @Test
    public void testJson1() {
        testJsonImpl1(templateList);
        testJsonImpl1(templateList2);
    }

    private static void testJsonImpl1(ReplaceTemplateList replaceTemplateList) {
        String filteredBody = replaceTemplateList.apply("" +
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
                "    \"extras\": {\n" +
                "      \"extra_name1\": \"extra_value1\",\n" +
                "      \"extra_name2\": \"extra_value2\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"client\": {\n" +
                "  }\n" +
                "}\n");
        assertEquals(filteredBody, "" +
                "{\n" +
                "  \"auth\": {\n" +
                "    \"id\": \"login\",\n" +
                "    \"auth_type\": \"PASSWORD\",\n" +
                "    \"client_soft\": \"soft\",\n" +
                "    \"key\": \"***\",\n" +
                "    \"timestamp\": \"2016-01-02T12:34:56.123Z\"\n" +
                "  },\n" +
                "  \"body\": {\n" +
                "    \"uid\": \"uid123\",\n" +
                "    \"comment\": \"комментарий\",\n" +
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
                "    \"extras\": {\n" +
                "      \"extra_name1\": \"extra_value1\",\n" +
                "      \"extra_name2\": \"extra_value2\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"client\": {\n" +
                "  }\n" +
                "}\n");
        assertThat(filteredBody).doesNotContain("password");
    }

    /**
     * С переносами строк
     */
    @Test
    public void testJson2() {
        testJsonImpl2(templateList);
        testJsonImpl2(templateList2);
    }

    private static void testJsonImpl2(ReplaceTemplateList replaceTemplateList) {
        String filteredBody = replaceTemplateList.apply("" +
                "{\n" +
                "  \"auth\": {\n" +
                "    \"id\": \"login\",\n" +
                "    \"auth_type\": \"PASSWORD\",\n" +
                "    \"client_soft\": \"soft\",\n" +
                "    \"key\"\n:\n\"password\",\n" +
                "    \"timestamp\": \"2016-01-02T12:34:56.123Z\"\n" +
                "  },\n" +
                "  \"body\": {\n" +
                "    \"uid\": \"uid123\",\n" +
                "    \"comment\": \"комментарий\",\n" +
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
                "    \"extras\": {\n" +
                "      \"extra_name1\": \"extra_value1\",\n" +
                "      \"extra_name2\": \"extra_value2\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"client\": {\n" +
                "  }\n" +
                "}\n");
        assertEquals(filteredBody, "" +
                "{\n" +
                "  \"auth\": {\n" +
                "    \"id\": \"login\",\n" +
                "    \"auth_type\": \"PASSWORD\",\n" +
                "    \"client_soft\": \"soft\",\n" +
                "    \"key\"\n:\n\"***\",\n" +
                "    \"timestamp\": \"2016-01-02T12:34:56.123Z\"\n" +
                "  },\n" +
                "  \"body\": {\n" +
                "    \"uid\": \"uid123\",\n" +
                "    \"comment\": \"комментарий\",\n" +
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
                "    \"extras\": {\n" +
                "      \"extra_name1\": \"extra_value1\",\n" +
                "      \"extra_name2\": \"extra_value2\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"client\": {\n" +
                "  }\n" +
                "}\n");
        assertThat(filteredBody).doesNotContain("password");
    }
    @Test
    public void testXmlPersonal() {
        String filteredBody = templateList.apply("" +
                "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<response>\n" +
                "  <result code=\"0\"/>\n" +
                "  <auth_result>authorized</auth_result>\n" +
                "  <personal>\n" +
                "    <last_name>Иванова</last_name>\n" +
                "    <first_name>Василиса</first_name>\n" +
                "    <patronymic>Геннадиевна</patronymic>\n" +
                "    <birth_date>1980-01-22</birth_date>\n" +
                "    <citizenship>RU</citizenship>\n" +
                "    <document>\n" +
                "      <type>1</type>\n" +
                "      <series>0000</series>\n" +
                "      <number>123456</number>\n" +
                "      <issued_date>2009-01-12</issued_date>\n" +
                "      <issued_authority_name>отделением по району бирюлево восточное оуфмс россии по гор. москве</issued_authority_name>\n" +
                "      <issued_authority_code>123-456</issued_authority_code>\n" +
                "    </document>\n" +
                "    <registration>\n" +
                "      <region>г Москва</region>\n" +
                "      <city>г Москва</city>\n" +
                "      <street>проезд Проездной</street>\n" +
                "      <house>1</house>\n" +
                "      <building>Корп 1</building>\n" +
                "      <flat>Кв 1</flat>\n" +
                "    </registration>\n" +
                "  </personal>\n" +
                "</response>\n");
        assertEquals(filteredBody, "" +
                "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<response>\n" +
                "  <result code=\"0\"/>\n" +
                "  <auth_result>authorized</auth_result>\n" +
                "  <personal>\n" +
                "    <last_name>Иванова</last_name>\n" +
                "    <first_name>Василиса</first_name>\n" +
                "    <patronymic>Геннадиевна</patronymic>\n" +
                "    <birth_date>1980-01-22</birth_date>\n" +
                "    <citizenship>RU</citizenship>\n" +
                "    <document>\n" +
                "      <type>1</type>\n" +
                "      <series>***</series>\n" +
                "      <number>***</number>\n" +
                "      <issued_date>***</issued_date>\n" +
                "      <issued_authority_name>отделением по району бирюлево восточное оуфмс россии по гор. москве</issued_authority_name>\n" +
                "      <issued_authority_code>123-456</issued_authority_code>\n" +
                "    </document>\n" +
                "    <registration>\n" +
                "      <region>г Москва</region>\n" +
                "      <city>г Москва</city>\n" +
                "      <street>проезд Проездной</street>\n" +
                "      <house>1</house>\n" +
                "      <building>Корп 1</building>\n" +
                "      <flat>Кв 1</flat>\n" +
                "    </registration>\n" +
                "  </personal>\n" +
                "</response>\n");
    }

    @Test
    public void testJsonPersonal() {
        String filteredBody = templateList.apply("" +
                "{\n" +
                "  \"result\" : {\n" +
                "    \"code\" : 0\n" +
                "  },\n" +
                "  \"auth_result\" : \"authorized\",\n" +
                "  \"personal\" : {\n" +
                "    \"last_name\" : \"Сергеев\",\n" +
                "    \"first_name\" : \"Сергей\",\n" +
                "    \"patronymic\" : \"Сергеевич\",\n" +
                "    \"birth_date\" : \"1985-02-22\",\n" +
                "    \"citizenship\" : \"RU\",\n" +
                "    \"document\" : {\n" +
                "      \"type\" : \"1\",\n" +
                "      \"series\" : \"1111\",\n" +
                "      \"number\" : \"222222\",\n" +
                "      \"issued_date\" : \"2001-00-00\",\n" +
                "      \"issued_authority_name\" : \"заволжским отделом внутренних дел города твери\",\n" +
                "      \"issued_authority_code\" : \"692-001\"\n" +
                "    },\n" +
                "    \"registration\" : {\n" +
                "      \"region\" : \"обл Тверская\",\n" +
                "      \"city\" : \"г Тверь\",\n" +
                "      \"street\" : \"ул Уличная\",\n" +
                "      \"house\" : \"1\",\n" +
                "      \"building\" : \"Корп 1\",\n" +
                "      \"flat\" : \"Кв 1\"\n" +
                "    }\n" +
                "  }\n" +
                "}\n");
        assertEquals(filteredBody, "" +
                "{\n" +
                "  \"result\" : {\n" +
                "    \"code\" : 0\n" +
                "  },\n" +
                "  \"auth_result\" : \"authorized\",\n" +
                "  \"personal\" : {\n" +
                "    \"last_name\" : \"Сергеев\",\n" +
                "    \"first_name\" : \"Сергей\",\n" +
                "    \"patronymic\" : \"Сергеевич\",\n" +
                "    \"birth_date\" : \"1985-02-22\",\n" +
                "    \"citizenship\" : \"RU\",\n" +
                "    \"document\" : {\n" +
                "      \"type\" : \"1\",\n" +
                "      \"series\" : \"***\",\n" +
                "      \"number\" : \"***\",\n" +
                "      \"issued_date\" : \"***\",\n" +
                "      \"issued_authority_name\" : \"заволжским отделом внутренних дел города твери\",\n" +
                "      \"issued_authority_code\" : \"692-001\"\n" +
                "    },\n" +
                "    \"registration\" : {\n" +
                "      \"region\" : \"обл Тверская\",\n" +
                "      \"city\" : \"г Тверь\",\n" +
                "      \"street\" : \"ул Уличная\",\n" +
                "      \"house\" : \"1\",\n" +
                "      \"building\" : \"Корп 1\",\n" +
                "      \"flat\" : \"Кв 1\"\n" +
                "    }\n" +
                "  }\n" +
                "}\n");
    }

    @Test
    public void testJsonIdentRequest() {
        String jsonIdentRequest = "" +
                "{\n" +
                "  \"auth\" : {\n" +
                "    \"id\" : \"id\",\n" +
                "    \"auth_type\" : \"PASSWORD\",\n" +
                "    \"key\" : \"ololo121212ABABA\",\n" +
                "    \"client_soft\" : \"test_soft\",\n" +
                "    \"timestamp\" : \"2018-01-19T06:27:57.559Z\"\n" +
                "  },\n" +
                "  \"personal\" : {\n" +
                "    \"last_name\" : \"Фамилия\",\n" +
                "    \"first_name\" : \"Имя\",\n" +
                "    \"patronymic\" : \"Отчество\",\n" +
                "    \"birth_date\" : \"1985-02-22\",\n" +
                "    \"birth_place\" : \"г. Минск, Респ. Беларусь\",\n" +
                "    \"citizenship\" : \"RU\",\n" +
                "    \"inn\" : \"690210300000\",\n" +
                "    \"document\" : {\n" +
                "      \"type\" : \"1\",\n" +
                "      \"series\" : \"1234\",\n" +
                "      \"number\" : \"567890 ая-АЯ.az-AZ\",\n" +
                "      \"issued_date\" : \"10.10.2010\",\n" +
                "      \"issued_authority_name\" : \"name\",\n" +
                "      \"issued_authority_code\" : \"code\"\n" +
                "    },\n" +
                "    \"registration\" : {\n" +
                "      \"zip_code\" : \"zip-code\",\n" +
                "      \"region\" : \"region\",\n" +
                "      \"city\" : \"city\",\n" +
                "      \"street\" : \"street\",\n" +
                "      \"house\" : \"house\",\n" +
                "      \"building\" : \"building\",\n" +
                "      \"flat\" : \"flat\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"client\" : {\n" +
                "    \"phone\" : 79030000000,\n" +
                "    \"email\" : \"email@example.com\"\n" +
                "  }\n" +
                "}";
        assertThat(templateList2.apply(jsonIdentRequest)).isEqualTo("" +
                "{\n" +
                "  \"auth\" : {\n" +
                "    \"id\" : \"id\",\n" +
                "    \"auth_type\" : \"PASSWORD\",\n" +
                "    \"key\" : \"***\",\n" +
                "    \"client_soft\" : \"test_soft\",\n" +
                "    \"timestamp\" : \"2018-01-19T06:27:57.559Z\"\n" +
                "  },\n" +
                "  \"personal\" : {\n" +
                "    \"last_name\" : \"Фамилия\",\n" +
                "    \"first_name\" : \"Имя\",\n" +
                "    \"patronymic\" : \"Отчество\",\n" +
                "    \"birth_date\" : \"1985-02-22\",\n" +
                "    \"birth_place\" : \"г. Минск, Респ. Беларусь\",\n" +
                "    \"citizenship\" : \"RU\",\n" +
                "    \"inn\" : \"690210300000\",\n" +
                "    \"document\" : {\n" +
                "      \"type\" : \"1\",\n" +
                "      \"series\" : \"***\",\n" +
                "      \"number\" : \"***\",\n" +
                "      \"issued_date\" : \"***\",\n" +
                "      \"issued_authority_name\" : \"name\",\n" +
                "      \"issued_authority_code\" : \"code\"\n" +
                "    },\n" +
                "    \"registration\" : {\n" +
                "      \"zip_code\" : \"zip-code\",\n" +
                "      \"region\" : \"region\",\n" +
                "      \"city\" : \"city\",\n" +
                "      \"street\" : \"street\",\n" +
                "      \"house\" : \"house\",\n" +
                "      \"building\" : \"building\",\n" +
                "      \"flat\" : \"flat\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"client\" : {\n" +
                "    \"phone\" : 79030000000,\n" +
                "    \"email\" : \"email@example.com\"\n" +
                "  }\n" +
                "}");
    }

    @Test
    public void testXmlLogMasker() {
        ReplaceTemplateList replaceTemplateList = new ReplaceTemplateList()
                .add(maskXmlTag("key"))
                .add(maskXmlTag("access_token"))
                .add(maskXmlTag("refresh_token"))
                .add(maskXmlExtra("new_password"))
                .add(maskXmlExtra("code"))
                .add(maskXmlExtra("oauth_code"))
                .add(maskXmlExtra("secret"));
        String xmlRequestBody = "" +
                "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<request>\n" +
                "  <auth>\n" +
                "    <id>id2</id>\n" +
                "    <auth_type>PASSWORD</auth_type>\n" +
                "    <key>!#-8@non</key>\n" +
                "    <client_soft>cf v1.0</client_soft>\n" +
                "    <language>en</language>\n" +
                "  </auth>\n" +
                "  <request_type>test_test_test</request_type>\n" +
                "  <access_token>abdbdBSBSB123</access_token>\n" +
                "  <refresh_token>abdbdBSBSB123</refresh_token>\n" +
                "  <extra name=\"new_password\">1234new_!pass</extra><extra name=\"code\">codecode]</extra>\n" +
                "  <extra name=\"oauth_code\">1234new_!pass</extra>" +
                "  <extra name=\"secret\">sekretiki</extra>" +
                "</request>\n";
        assertThat(replaceTemplateList.apply(xmlRequestBody)).isEqualTo("" +
                "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<request>\n" +
                "  <auth>\n" +
                "    <id>id2</id>\n" +
                "    <auth_type>PASSWORD</auth_type>\n" +
                "    <key>***</key>\n" +
                "    <client_soft>cf v1.0</client_soft>\n" +
                "    <language>en</language>\n" +
                "  </auth>\n" +
                "  <request_type>test_test_test</request_type>\n" +
                "  <access_token>***</access_token>\n" +
                "  <refresh_token>***</refresh_token>\n" +
                "  <extra name=\"new_password\">***</extra><extra name=\"code\">***</extra>\n" +
                "  <extra name=\"oauth_code\">***</extra>" +
                "  <extra name=\"secret\">***</extra>" +
                "</request>\n");
    }
}
