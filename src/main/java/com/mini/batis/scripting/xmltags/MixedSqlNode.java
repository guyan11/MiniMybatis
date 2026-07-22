package com.mini.batis.scripting.xmltags;

import java.util.List;

public class MixedSqlNode implements SqlNode {

    private final List<SqlNode> sqlNodes;

    public MixedSqlNode(List<SqlNode> sqlNodes) {
        this.sqlNodes = sqlNodes;
    }

    @Override
    public boolean apply(DynamicContext context) {
        sqlNodes.forEach(sqlNode -> sqlNode.apply(context));
        return true;
    }
}
