package com.mini.batis.scripting.xmltags;

import com.mini.batis.scripting.SqlSourceBuilder;

public class TextSqlNode implements SqlNode {

    private final String text;

    private final SqlSourceBuilder sqlSourceBuilder = new SqlSourceBuilder();

    public TextSqlNode(String text) {
        this.text = text;
    }

    @Override
    public boolean apply(DynamicContext context) {
        String sql = sqlSourceBuilder.parseDollarPlaceholder(text,
                context.getParameterObject());
        context.appendSql(sql);
        return true;
    }

    public boolean isDynamic() {
        return text != null && text.contains("${");
    }

}
