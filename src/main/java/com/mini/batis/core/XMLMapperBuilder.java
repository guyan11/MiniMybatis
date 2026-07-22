package com.mini.batis.core;

import com.mini.batis.model.Configuration;
import com.mini.batis.model.MapperStatement;
import com.mini.batis.scripting.SqlSource;
import com.mini.batis.scripting.defaults.DynamicSqlSource;
import com.mini.batis.scripting.defaults.RawSqlSource;
import com.mini.batis.scripting.xmltags.MixedSqlNode;
import com.mini.batis.scripting.xmltags.StaticTextSqlNode;
import com.mini.batis.scripting.xmltags.TextSqlNode;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class XMLMapperBuilder {

    public void parse(InputStream inputStream, Configuration configuration) throws Exception {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(inputStream);
        Element mapperElement = document.getRootElement();
        parseMapper(mapperElement, configuration);
    }

    private void parseMapper(Element mapperElement, Configuration configuration) {
        String namespace = mapperElement.attributeValue("namespace");
        if (namespace == null || namespace.trim().isEmpty()) {
            throw new RuntimeException("namespace is empty");
        }

        List<String> statementTags = Arrays.asList("select", "insert", "update", "delete");
        for (String statementTag : statementTags) {
            List<Element> statementElements = mapperElement.elements(statementTag);
            for (Element statementElement : statementElements) {
                MapperStatement mapperStatement = buildMapperStatement(statementElement, namespace, statementTag);
                configuration.addMapperStatement(namespace, mapperStatement);
            }
        }
    }

    public SqlSource createSqlSource(String sql) {

        TextSqlNode textSqlNode = new TextSqlNode(sql);

        if (textSqlNode.isDynamic()) {
            MixedSqlNode rootSqlNode = new MixedSqlNode(Collections.singletonList(textSqlNode));
            return new DynamicSqlSource(rootSqlNode);
        }
        return new RawSqlSource(sql);
    }

    private MapperStatement buildMapperStatement(Element element, String namespace, String sqlCommandType) {
        String id = element.attributeValue("id");
        String resultType = element.attributeValue("resultType");
        String parameterType = element.attributeValue("parameterType");
        String sql = element.getText();
        String statementType = element.attributeValue("statementType");

        return MapperStatement.builder()
                .namespace(namespace)
                .id(id)
                .resultType(resultType)
                .parameterType(parameterType)
                .sql(sql)
                .sqlCommandType(sqlCommandType)
                .statementType(statementType)
                .sqlSource(createSqlSource(sql))
                .build();
    }
}
