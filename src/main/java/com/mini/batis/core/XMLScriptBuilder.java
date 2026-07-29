package com.mini.batis.core;

import com.mini.batis.scripting.SqlSource;
import com.mini.batis.scripting.defaults.DynamicSqlSource;
import com.mini.batis.scripting.defaults.RawSqlSource;
import com.mini.batis.scripting.xmltags.MixedSqlNode;
import com.mini.batis.scripting.xmltags.TextSqlNode;
import org.dom4j.Element;

import java.util.Collections;

public class XMLScriptBuilder {

    private final Element element;

    public XMLScriptBuilder(Element element) {
        this.element = element;
    }

    public SqlSource parseScriptNode() {
        String sql = element.getText();

        TextSqlNode textSqlNode = new TextSqlNode(sql);
        if (textSqlNode.isDynamic()) {
            MixedSqlNode rootSqlNode =
                    new MixedSqlNode(Collections.singletonList(textSqlNode));
            return new DynamicSqlSource(rootSqlNode);
        }

        return new RawSqlSource(sql);
    }
}
