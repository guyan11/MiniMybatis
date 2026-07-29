package com.mini.batis.core;

import com.mini.batis.model.Configuration;
import com.mini.batis.model.MapperStatement;
import com.mini.batis.scripting.SqlSource;
import org.dom4j.Element;

public class XMLStatementBuilder {

    private final Configuration configuration;
    private final Element element;
    private final String namespace;
    private final String sqlCommandType;

    public XMLStatementBuilder(Configuration configuration,
                               Element statementElement, String namespace,
                               String statementTag) {

        this.configuration = configuration;
        this.element = statementElement;
        this.namespace = namespace;
        this.sqlCommandType = statementTag;
    }

    public void parseStatementNode() {
        String id = element.attributeValue("id");
        String resultType = element.attributeValue("resultType");
        String parameterType = element.attributeValue("parameterType");
        String statementType = element.attributeValue("statementType", "preparedStatement");

        XMLScriptBuilder scriptBuilder = new XMLScriptBuilder(element);
        SqlSource sqlSource = scriptBuilder.parseScriptNode();

        MapperStatement mapperStatement = MapperStatement.builder()
                .namespace(namespace)
                .id(id)
                .statementId(namespace + "." + id)
                .resultType(resultType)
                .parameterType(parameterType)
                .sqlCommandType(sqlCommandType)
                .statementType(statementType)
                .sqlSource(sqlSource)
                .build();

        configuration.addMapperStatement(namespace, mapperStatement);
    }


}
