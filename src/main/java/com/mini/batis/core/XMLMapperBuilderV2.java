package com.mini.batis.core;

import com.mini.batis.model.Configuration;
import com.mini.batis.model.MapperStatement;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class XMLMapperBuilderV2 {

    public void parse(InputStream is, Configuration configuration) throws Exception {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(is);
        Element mapperElement = document.getRootElement();
        parseMapper(mapperElement, configuration);
    }


    private void parseMapper(Element mapperElement, Configuration configuration) {
        String namespace = mapperElement.attributeValue("namespace");
        if (null == namespace || namespace.trim().isEmpty()) {
            throw new RuntimeException("namespace is empty");
        }

        List<String> statementTags = Arrays.asList("select", "insert", "update", "delete");
        for (String tag : statementTags) {
            List<Element> elements = mapperElement.elements(tag);
            if (null == elements || elements.isEmpty()) {
                continue;
            }
            for (Element element : elements) {
                MapperStatement mappedStatement = buildMapperStatement(element, namespace, tag);
                configuration.addMapperStatement(namespace, mappedStatement);
            }
        }
    }

    private static MapperStatement buildMapperStatement(Element element, String namespace, String sqlCommandType) {
        String id = element.attributeValue("id");
        String resultType = element.attributeValue("resultType");
        String parameterType = element.attributeValue("parameterType");
        String selText = element.getText();
        String statementType = element.attributeValue("statementType");

        return MapperStatement.builder()
                .namespace(namespace)
                .id(id)
                .resultType(resultType)
                .parameterType(parameterType)
                .sql(selText)
                .sqlCommandType(sqlCommandType)
                .statementType(statementType)
                .build();
    }
}
