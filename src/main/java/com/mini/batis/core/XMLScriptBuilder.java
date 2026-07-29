package com.mini.batis.core;

import com.mini.batis.scripting.SqlSource;
import com.mini.batis.scripting.defaults.DynamicSqlSource;
import com.mini.batis.scripting.defaults.RawSqlSource;
import com.mini.batis.scripting.xmltags.MixedSqlNode;
import com.mini.batis.scripting.xmltags.SqlNode;
import com.mini.batis.scripting.xmltags.StaticTextSqlNode;
import com.mini.batis.scripting.xmltags.TextSqlNode;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XMLScriptBuilder {

    private final Element element;

    private boolean isDynamic;

    public XMLScriptBuilder(Element element) {
        this.element = element;
    }

    public SqlSource parseScriptNode() {
        // String sql = element.getText();
        //
        // TextSqlNode textSqlNode = new TextSqlNode(sql);
        // if (textSqlNode.isDynamic()) {
        //     MixedSqlNode rootSqlNode =
        //             new MixedSqlNode(Collections.singletonList(textSqlNode));
        //     return new DynamicSqlSource(rootSqlNode);
        // }
        //
        // return new RawSqlSource(sql);

        MixedSqlNode rootSqlNode = parseDynamicTags(element);
        if (isDynamic) {
            return new DynamicSqlSource(rootSqlNode);
        }
        return new RawSqlSource(rootSqlNode);
    }

    public MixedSqlNode parseDynamicTags(Element element) {
        List<SqlNode> contents = new ArrayList<>();
        List<Node> children = element.content();
        for (Node child : children) {
            if (child.getNodeType() == Node.CDATA_SECTION_NODE || child.getNodeType() == Node.TEXT_NODE) {
                String data = child.getText();

                if (data == null || data.trim().isEmpty()) {
                    continue;
                }

                TextSqlNode textSqlNode = new TextSqlNode(data);
                if (textSqlNode.isDynamic()) {
                    contents.add(textSqlNode);
                    isDynamic = true;
                } else {
                    StaticTextSqlNode staticTextSqlNode = new StaticTextSqlNode(data);
                    contents.add(staticTextSqlNode);
                }
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                throw new RuntimeException("unsupported element: " + child.getName());
            }
        }
        return new MixedSqlNode(contents);
    }

}
