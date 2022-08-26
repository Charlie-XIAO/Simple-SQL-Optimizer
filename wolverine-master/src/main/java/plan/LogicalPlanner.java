package plan;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import parser.SqlBaseBaseVisitor;
import parser.SqlBaseParser.*;
import plan.type.JoinType;

import static parser.SqlBaseParser.BY;
import static parser.SqlBaseParser.DESC;
import static parser.SqlBaseParser.FROM;
import static parser.SqlBaseParser.GROUP;
import static parser.SqlBaseParser.HAVING;
import static parser.SqlBaseParser.LIMIT;
import static parser.SqlBaseParser.ORDER;
import static parser.SqlBaseParser.WHERE;

public class LogicalPlanner extends SqlBaseBaseVisitor {
    OutputNode root = new OutputNode();
    Node current = root;

    @Override
    public Object visitSingleStatement(SingleStatementContext ctx) {
        visitChildren(ctx);
        return root;
    }

    @Override
    public Object visitStatementDefault(StatementDefaultContext ctx) {
        visitChildren(ctx);
        return null;
    }

    @Override
    public Object visitQuery(QueryContext ctx) {
        visitChildren(ctx);
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitQueryNoWith(QueryNoWithContext ctx) {
        searchLimit(ctx);
        searchOrder(ctx);
        ctx.getChild(0).accept(this);
        return null;
    }

    @Override
    public Object visitQueryTermDefault(QueryTermDefaultContext ctx) {
        visitChildren(ctx);
        return null;
    }

    @Override
    public Object visitQueryPrimaryDefault(QueryPrimaryDefaultContext ctx) {
        visitChildren(ctx);
        return null;
    }

    public Object visitQuerySpecification(QuerySpecificationContext ctx) {
        searchSelect(ctx);
        searchHaving(ctx);
        searchGroup(ctx);
        searchWhere(ctx);
        searchFrom(ctx);
        return null;
    }

    private void searchLimit(QueryNoWithContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNodeImpl) {
                Token token = ((TerminalNodeImpl) child).getSymbol();
                if (token.getType() == LIMIT) {
                    if (ctx.getChild(i + 1) == null) {
                        throw new RuntimeException("Limit must have an integer value");
                    } else {
                        LimitNode limitNode = new LimitNode(Integer.parseInt(ctx.getChild(i + 1).getText()));
                        addNode(limitNode);
                    }
                    break;
                }
            }
        }
    }

    private void searchOrder(QueryNoWithContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNodeImpl) {
                Token token = ((TerminalNodeImpl) child).getSymbol();
                if (token.getType() == ORDER) {
                    if (ctx.getChild(i + 1) instanceof TerminalNodeImpl) {
                        Token token2 = ((TerminalNodeImpl) ctx.getChild(i + 1)).getSymbol();
                        if (token2.getType() == BY) {
                            SortNode sortNode = new SortNode();
                            ParseTree afterBy = ctx.getChild(i + 2);
                            if (afterBy instanceof SortItemContext) {
                                sortNode.addSortItem(parseSortItemContext((SortItemContext) afterBy));
                                for (int j = i + 3; j < ctx.getChildCount(); j += 2) {
                                    if (ctx.getChild(j) instanceof TerminalNodeImpl) {
                                        Token token3 = ((TerminalNodeImpl) ctx.getChild(j)).getSymbol();
                                        if (",".equals(token3.getText())) {
                                            sortNode.addSortItem(
                                                parseSortItemContext((SortItemContext) ctx.getChild(j + 1)));
                                        }
                                    }
                                }
                            } else {
                                throw new RuntimeException("Order must have at least one sort item");
                            }
                            addNode(sortNode);
                            break;
                        } else {
                            throw new RuntimeException("Order must have a BY clause");
                        }
                    }
                }
            }
        }
    }

    private SortItem parseSortItemContext(SortItemContext sortItemContext) {
        Pair<AggregateFuncType, Pair<String, String>> tableColumn = parseTableColumn(
            (PrimaryExpressionContext) sortItemContext.getChild(0).getChild(0).getChild(0).getChild(0));
        Boolean isDesc = false;
        if (sortItemContext.getChild(1) instanceof TerminalNodeImpl) {
            Token token = ((TerminalNodeImpl) sortItemContext.getChild(1)).getSymbol();
            if (token.getType() == DESC) {
                isDesc = true;
            }
        }
        return new SortItem(tableColumn.a, tableColumn.b.a, tableColumn.b.b, isDesc);
    }

    private Pair<AggregateFuncType, Pair<String, String>> parseTableColumn(PrimaryExpressionContext ctx) {
        int childCount = ctx.getChildCount();
        if (childCount == 3) {
            return new Pair<>(null, new Pair<>(ctx.getChild(0).getText(), ctx.getChild(2).getText()));
        } else if (childCount == 1) {
            return new Pair<>(null, new Pair<>(null, ctx.getChild(2).getText()));
        } else {
            AggregateFuncType aggregateFuncType = AggregateFuncType.valueOf(ctx.getChild(0).getText());
            PrimaryExpressionContext ctx1 =
                (PrimaryExpressionContext) ctx.getChild(2).getChild(0).getChild(0).getChild(0);
            int childCount1 = ctx1.getChildCount();
            if (childCount1 == 3) {
                return new Pair<>(aggregateFuncType,
                    new Pair<>(ctx1.getChild(0).getText(), ctx1.getChild(2).getText()));
            } else if (childCount1 == 1) {
                return new Pair<>(aggregateFuncType, new Pair<>(null, ctx1.getChild(2).getText()));
            } else {
                throw new RuntimeException("Invalid table column");
            }
        }
    }

    private void searchSelect(QuerySpecificationContext ctx) {
        ProjectNode projectNode = new ProjectNode();
        ParseTree afterSelect = ctx.getChild(1);
        if (afterSelect instanceof SelectItemContext) {
            projectNode.addItem(parseSelectItemContext((SelectItemContext) afterSelect));
            for (int i = 2; i < ctx.getChildCount(); i += 2) {
                if (ctx.getChild(i) instanceof TerminalNodeImpl) {
                    Token token = ((TerminalNodeImpl) ctx.getChild(i)).getSymbol();
                    if (",".equals(token.getText())) {
                        projectNode.addItem(parseSelectItemContext((SelectItemContext) ctx.getChild(i + 1)));
                    }
                }
            }
        } else {
            throw new RuntimeException("Select must have at least one select item");
        }
        addNode(projectNode);
    }

    private void addNode(Node node) {
        node.setHeight(current.getHeight() + 1);
        current.setChild(node);
        current = node;
    }

    private ProjectItem parseSelectItemContext(SelectItemContext ctx) {
        Pair<AggregateFuncType, Pair<String, String>> aggTableColumn = parseTableColumn(
            (PrimaryExpressionContext) ctx.getChild(0).getChild(0).getChild(0).getChild(0));
        return new ProjectItem(aggTableColumn.a, aggTableColumn.b.a, aggTableColumn.b.b);
    }

    private void searchHaving(QuerySpecificationContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNodeImpl) {
                Token token = ((TerminalNodeImpl) child).getSymbol();
                if (token.getType() == HAVING) {
                    ParseTree afterHaving = ctx.getChild(i + 1);
                    if (afterHaving == null) {
                        throw new RuntimeException("Having must have a condition");
                    } else {
                        HavingNode havingNode = new HavingNode();
                        HavingItem havingItem = new HavingItem();
                        havingItem.setComparison(Comparison.valueOf(afterHaving.getChild(1).getChild(0).getText()));
                        havingItem.setComLiteral(afterHaving.getChild(1).getChild(1).getText());
                        Pair<AggregateFuncType, Pair<String, String>> aggTableColumn = parseTableColumn(
                            (PrimaryExpressionContext) (afterHaving.getChild(0).getChild(0)));
                        havingItem.setProjectItem(
                            new ProjectItem(aggTableColumn.a, aggTableColumn.b.a, aggTableColumn.b.b));
                        havingNode.addItem(havingItem);
                        addNode(havingNode);
                        break;
                    }
                }
            }
        }
    }

    private void searchGroup(QuerySpecificationContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNodeImpl) {
                Token token = ((TerminalNodeImpl) child).getSymbol();
                if (token.getType() == GROUP) {
                    if (ctx.getChild(i + 1) instanceof TerminalNodeImpl) {
                        Token token1 = ((TerminalNodeImpl) ctx.getChild(i + 1)).getSymbol();
                        if (token1.getType() == BY) {
                            GroupByNode groupNode = new GroupByNode();
                            ParseTree afterBy = ctx.getChild(i + 2);
                            ParseTree groupElement = afterBy.getChild(0);
                            if (groupElement instanceof GroupingElementContext) {
                                groupNode.addItem(parseGroupingELementContext((GroupingElementContext) groupElement));
                            } else {
                                throw new RuntimeException("Group by must have at least one group by item");
                            }
                            for (int j = 1; j < afterBy.getChildCount(); j += 2) {
                                if (afterBy.getChild(j) instanceof TerminalNodeImpl) {
                                    Token token2 = ((TerminalNodeImpl) afterBy.getChild(j)).getSymbol();
                                    if (",".equals(token2.getText())) {
                                        groupNode.addItem(parseGroupingELementContext(
                                            (GroupingElementContext) afterBy.getChild(j + 1)));
                                    }
                                }
                            }
                            addNode(groupNode);
                            break;
                        } else {
                            throw new RuntimeException("Group must have a BY clause");
                        }
                    }
                }
            }
        }
    }

    private GroupByItem parseGroupingELementContext(GroupingElementContext ctx) {
        PrimaryExpressionContext primaryExpressionContext =
            (PrimaryExpressionContext) ctx.getChild(0).getChild(0).getChild(0).getChild(0).getChild(0);
        Pair<AggregateFuncType, Pair<String, String>> aggTableColumn = parseTableColumn(primaryExpressionContext);
        return new GroupByItem(aggTableColumn.b.a, aggTableColumn.b.b);
    }

    private void searchWhere(QuerySpecificationContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNodeImpl) {
                Token token = ((TerminalNodeImpl) child).getSymbol();
                if (token.getType() == WHERE) {
                    ParseTree afterWhere = ctx.getChild(i + 1);
                    if (afterWhere == null) {
                        throw new RuntimeException("Where must have a condition");
                    } else {
                        FilterNode filterNode = new FilterNode();
                        parseBooleanExpression((BooleanExpressionContext) afterWhere, filterNode);
                        addNode(filterNode);
                        break;
                    }
                }
            }
        }
    }

    private void parseBooleanExpression(BooleanExpressionContext ctx, FilterNode filterNode) {
        int childCount = ctx.getChildCount();
        if (childCount == 3) {
            parseBooleanExpression((BooleanExpressionContext) ctx.getChild(0), filterNode);
            parseBooleanExpression((BooleanExpressionContext) ctx.getChild(2), filterNode);
        } else if (childCount == 2) {
            PrimaryExpressionContext primaryExpressionContext =
                (PrimaryExpressionContext) ctx.getChild(0).getChild(0);
            Pair<AggregateFuncType, Pair<String, String>> aggTableColumn = parseTableColumn(primaryExpressionContext);
            FilterItem filterItem = new FilterItem();
            filterItem.setTableName(aggTableColumn.b.a);
            filterItem.setColumnName(aggTableColumn.b.b);
            filterItem.setComparison(Comparison.valueOf(ctx.getChild(1).getChild(0).getText()));
            filterItem.setComLiteral(ctx.getChild(1).getChild(1).getText());
            filterNode.addItem(filterItem);
        } else {
            throw new RuntimeException("Boolean expression must have at least one condition");
        }
    }

    private void searchFrom(QuerySpecificationContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNodeImpl) {
                Token token = ((TerminalNodeImpl) child).getSymbol();
                if (token.getType() == FROM) {
                    ParseTree afterFrom = ctx.getChild(i + 1);
                    if (afterFrom == null) {
                        throw new RuntimeException("From must have a table");
                    } else {
                        parseRelation((RelationContext) afterFrom, current);
                        break;
                    }
                }
            }
        }
    }

    private void parseRelation(RelationContext ctx, Node parent) {
        int childCount = ctx.getChildCount();
        if (childCount == 1) {
            ScanNode scanNode = new ScanNode();
            scanNode.setHeight(parent.getHeight() + 1);
            scanNode.setTableName(ctx.getChild(0).getText());
            if (parent.isJoinNode()) {
                if (((JoinNode) parent).getLeft() == null) {
                    ((JoinNode) parent).setLeft(scanNode);
                } else {
                    ((JoinNode) parent).setRight(scanNode);
                }
            } else {
                parent.setChild(scanNode);
            }
        } else if (childCount == 5) {
            JoinNode joinNode = new JoinNode();
            joinNode.setHeight(parent.getHeight() + 1);
            joinNode.setJoinType(JoinType.valueOf(ctx.getChild(1).getText()));
            PrimaryExpressionContext primaryExpressionContext =
                (PrimaryExpressionContext) ctx.getChild(4).getChild(1).getChild(0).getChild(0);
            Pair<AggregateFuncType, Pair<String, String>> aggTableColumn = parseTableColumn(primaryExpressionContext);
            joinNode.setTableNameLeft(aggTableColumn.b.a);
            joinNode.setColumnNameLeft(aggTableColumn.b.b);
            PrimaryExpressionContext primaryExpressionContext2 =
                (PrimaryExpressionContext) ctx.getChild(4)
                    .getChild(1)
                    .getChild(1)
                    .getChild(1)
                    .getChild(0);
            Pair<AggregateFuncType, Pair<String, String>> aggTableColumn2 = parseTableColumn(primaryExpressionContext2);
            joinNode.setTableNameRight(aggTableColumn2.b.a);
            joinNode.setColumnNameRight(aggTableColumn2.b.b);
            parseRelation((RelationContext) ctx.getChild(0), joinNode);
            parseRelation((RelationContext) ctx.getChild(3), joinNode);
            if (parent.isJoinNode()) {
                if (((JoinNode) parent).getLeft() == null) {
                    ((JoinNode) parent).setLeft(joinNode);
                } else {
                    ((JoinNode) parent).setRight(joinNode);
                }
            } else {
                parent.setChild(joinNode);
            }
        } else {
            throw new RuntimeException("Relation must have at least one table");
        }
    }
}
