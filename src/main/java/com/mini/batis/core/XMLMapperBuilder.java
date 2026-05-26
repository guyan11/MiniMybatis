package com.mini.batis.core;

import com.mini.batis.model.Configuration;
import com.mini.batis.model.MapperStatement;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

public class XMLMapperBuilder {

    public void parse(InputStream is, Configuration configuration) throws Exception {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(is);
        Element mapperElement = document.getRootElement();
        parseMapper(mapperElement, configuration);
    }


    private void parseMapper(Element mapperElement, Configuration configuration) {
        String namespace = mapperElement.attributeValue("namespace");
        if (null == namespace || namespace.isEmpty()) {
            return;
        }
        List<Element> selects = mapperElement.elements("select");
        if (null == selects || selects.isEmpty()) {
            return;
        }

        for (Element select : selects) {
            MapperStatement mappedStatement = buildMapperStatement(select, namespace);
            configuration.addMapperStatement(namespace, mappedStatement);
        }
    }

    private static MapperStatement buildMapperStatement(Element select, String namespace) {
        String id = select.attributeValue("id");
        String resultType = select.attributeValue("resultType");
        String parameterType = select.attributeValue("parameterType");
        String selText = select.getText();
        String sqlCommandType = select.attributeValue("sqlCommandType");
        String statementType = select.attributeValue("statementType");
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
