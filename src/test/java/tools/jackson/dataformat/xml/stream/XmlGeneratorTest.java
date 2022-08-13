package tools.jackson.dataformat.xml.stream;

import java.io.*;

import javax.xml.namespace.QName;

import tools.jackson.dataformat.xml.XmlMapper;
import tools.jackson.dataformat.xml.XmlTestBase;
import tools.jackson.dataformat.xml.ser.ToXmlGenerator;

public class XmlGeneratorTest extends XmlTestBase
{
    private final XmlMapper MAPPER = xmlMapper(true);

    public void testSimpleElement() throws Exception
    {
        StringWriter out = new StringWriter();
        ToXmlGenerator gen = (ToXmlGenerator) MAPPER.createGenerator(out);
        // root name is special, need to be fed first:
        gen.setNextName(new QName("root"));
        gen.writeStartObject();
        gen.writeName("elem");
        gen.writeString("value");
        gen.writeEndObject();
        gen.close();
        String xml = out.toString();
        // one more thing: remove that annoying 'xmlns' decl, if it's there:
        xml = removeSjsxpNamespace(xml);
        assertEquals("<root><elem>value</elem></root>", xml);
    }

    public void testNullValuedElement() throws Exception
    {
        // First explicitly written
        StringWriter out = new StringWriter();
        ToXmlGenerator gen = (ToXmlGenerator) MAPPER.createGenerator(out);
        gen.setNextName(new QName("root"));
        gen.writeStartObject();
        gen.writeName("elem");
        gen.writeNull();
        gen.writeEndObject();
        gen.close();
        String xml = removeSjsxpNamespace(out.toString());
        assertEquals("<root><elem/></root>", xml);

        // and then indirectly (see [dataformat-xml#413])
        out = new StringWriter();
        gen = (ToXmlGenerator) MAPPER.createGenerator(out);
        gen.setNextName(new QName("root"));
        gen.writeStartObject();
        gen.writeName("elem");
        gen.writeString((String) null);
        gen.writeEndObject();
        gen.close();
        xml = removeSjsxpNamespace(out.toString());
        assertEquals("<root><elem/></root>", xml);
    }
    
    public void testSimpleAttribute() throws Exception
    {
        StringWriter out = new StringWriter();
        ToXmlGenerator gen = (ToXmlGenerator) MAPPER.createGenerator(out);
        // root name is special, need to be fed first:
        gen.setNextName(new QName("root"));
        gen.writeStartObject();
        // and also need to force attribute
        gen.setNextIsAttribute(true);
        gen.writeName("attr");
        gen.writeString("value");
        gen.writeEndObject();
        gen.close();
        String xml = out.toString();
        // one more thing: remove that annoying 'xmlns' decl, if it's there:
        xml = removeSjsxpNamespace(xml);
        assertEquals("<root attr=\"value\"/>", xml);
    }

    public void testSecondLevelAttribute() throws Exception
    {
        StringWriter out = new StringWriter();
        ToXmlGenerator gen = (ToXmlGenerator) MAPPER.createGenerator(out);
        gen.setNextName(new QName("root"));
        gen.writeStartObject();
        gen.writeName("elem");
        gen.writeStartObject();
        // and also need to force attribute
        gen.setNextIsAttribute(true);
        gen.writeName("attr");
        gen.writeString("value");
        gen.writeEndObject();
        gen.writeEndObject();
        gen.close();
        String xml = out.toString();
        // one more thing: remove that annoying 'xmlns' decl, if it's there:
        xml = removeSjsxpNamespace(xml);
        assertEquals("<root><elem attr=\"value\"/></root>", xml);
    }

    public void testAttrAndElem() throws Exception
    {
        StringWriter out = new StringWriter();
        ToXmlGenerator gen = (ToXmlGenerator) MAPPER.createGenerator(out);
        gen.setNextName(new QName("root"));
        gen.writeStartObject();
        // and also need to force attribute
        gen.writeName("attr");
        gen.setNextIsAttribute(true);
        gen.writeNumber(-3);

        // Also let's add child element as well
        gen.setNextIsAttribute(false);
        gen.writeName("elem");
        gen.writeNumber(13);
        gen.writeEndObject();
        gen.close();
        String xml = removeSjsxpNamespace(out.toString());
        assertEquals("<root attr=\"-3\"><elem>13</elem></root>", xml);
    }

    // [Issue#6], missing overrides for File-backed generator
    public void testWriteToFile() throws Exception
    {
        File f = File.createTempFile("test", ".tst");
        MAPPER.writeValue(f, new IntWrapper(42));

        String xml = readAll(f).trim();

        assertEquals("<IntWrapper><i>42</i></IntWrapper>", xml);
        f.delete();
    }

    public void testRawSimpleValue() throws Exception
    {
        StringWriter out = new StringWriter();
        ToXmlGenerator gen = (ToXmlGenerator) MAPPER.createGenerator(out);
        // root name is special, need to be fed first:
        gen.setNextName(new QName("root"));
        gen.writeStartObject();
        gen.writeName("elem");
        gen.writeRawValue("value");
        gen.writeEndObject();
        gen.close();
        String xml = out.toString();
        // one more thing: remove that annoying 'xmlns' decl, if it's there:
        xml = removeSjsxpNamespace(xml);
        assertEquals("<root><elem>value</elem></root>", xml);
    }

    public void testRawOffsetValue() throws Exception
    {
        StringWriter out = new StringWriter();
        ToXmlGenerator gen = (ToXmlGenerator) MAPPER.createGenerator(out);
        // root name is special, need to be fed first:
        gen.setNextName(new QName("root"));
        gen.writeStartObject();
        gen.writeName("elem");
        gen.writeRawValue("NotAValue_value_NotAValue", 10, 5);
        gen.writeEndObject();
        gen.close();
        String xml = out.toString();
        // one more thing: remove that annoying 'xmlns' decl, if it's there:
        xml = removeSjsxpNamespace(xml);
        assertEquals("<root><elem>value</elem></root>", xml);
    }

    public void testRawCharArrayValue() throws Exception
    {
        StringWriter out = new StringWriter();
        ToXmlGenerator gen = (ToXmlGenerator) MAPPER.createGenerator(out);
        // root name is special, need to be fed first:
        gen.setNextName(new QName("root"));
        gen.writeStartObject();
        gen.writeName("elem");
        gen.writeRawValue(new char[] {'!', 'v', 'a', 'l', 'u', 'e', '!'}, 1, 5);
        gen.writeEndObject();
        gen.close();
        String xml = out.toString();
        // one more thing: remove that annoying 'xmlns' decl, if it's there:
        xml = removeSjsxpNamespace(xml);
        assertEquals("<root><elem>value</elem></root>", xml);
    }

    public void testRawSimpleAttribute() throws Exception
    {
        StringWriter out = new StringWriter();
        ToXmlGenerator gen = (ToXmlGenerator) MAPPER.createGenerator(out);
        // root name is special, need to be fed first:
        gen.setNextName(new QName("root"));
        gen.writeStartObject();
        // and also need to force attribute
        gen.setNextIsAttribute(true);
        gen.writeName("attr");
        gen.writeRawValue("value");
        gen.writeEndObject();
        gen.close();
        String xml = out.toString();
        // one more thing: remove that annoying 'xmlns' decl, if it's there:
        xml = removeSjsxpNamespace(xml);
        assertEquals("<root attr=\"value\"/>", xml);
    }

    public void testRawOffsetAttribute() throws Exception
    {
        StringWriter out = new StringWriter();
        ToXmlGenerator gen = (ToXmlGenerator) MAPPER.createGenerator(out);
        // root name is special, need to be fed first:
        gen.setNextName(new QName("root"));
        gen.writeStartObject();
        // and also need to force attribute
        gen.setNextIsAttribute(true);
        gen.writeName("attr");
        gen.writeRawValue("NotAValue_value_NotAValue", 10, 5);
        gen.writeEndObject();
        gen.close();
        String xml = out.toString();
        // one more thing: remove that annoying 'xmlns' decl, if it's there:
        xml = removeSjsxpNamespace(xml);
        assertEquals("<root attr=\"value\"/>", xml);
    }

    public void testRawCharArratAttribute() throws Exception
    {
        StringWriter out = new StringWriter();
        ToXmlGenerator gen = (ToXmlGenerator) MAPPER.createGenerator(out);
        // root name is special, need to be fed first:
        gen.setNextName(new QName("root"));
        gen.writeStartObject();
        // and also need to force attribute
        gen.setNextIsAttribute(true);
        gen.writeName("attr");
        gen.writeRawValue(new char[]{'!', 'v', 'a', 'l', 'u', 'e', '!'}, 1, 5);
        gen.writeEndObject();
        gen.close();
        String xml = out.toString();
        // one more thing: remove that annoying 'xmlns' decl, if it's there:
        xml = removeSjsxpNamespace(xml);
        assertEquals("<root attr=\"value\"/>", xml);
    }
}