import RBO.RBOptimizer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import CBO.JoinReorder;
import parser.SqlBaseLexer;
import parser.SqlBaseParser;
import plan.LogicalPlanner;
import plan.Node;
import plan.OutputNode;
import plan.ScanNode;
import table.Record;

import java.util.Map;
import java.util.Iterator;
import java.util.List;

public class Launcher {

    private static void ParserTest() throws Exception {
        String sqlText =
            "SELECT students.name, courses.name "
                + "FROM students INNER JOIN courses ON students.sid = courses.sid "
                + "WHERE students.sid = 1 "
                + "GROUP BY students.name "
                + "HAVING SUM(courses.credit) > 3 "
                + "ORDER BY students.name LIMIT 1";
        LogicalPlanner builder = new LogicalPlanner();
        SqlBaseLexer lexer = new SqlBaseLexer(CharStreams.fromString(sqlText.toUpperCase()));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        SqlBaseParser parser = new SqlBaseParser(tokenStream);
        OutputNode originalLogicalPlan = (OutputNode) builder.visit(parser.singleStatement());
        Map<Integer, List<Node>> logicalPlan = originalLogicalPlan.getLogicalPlan();
        ScanNode scanNode = (ScanNode) logicalPlan.get(8).get(1);
        Iterator<Record> iterator = scanNode.iterator();
    }

    private static void RBOTest() throws Exception {
        String sqlText =
            "SELECT students.name, courses.name "
                + "FROM students INNER JOIN courses ON students.sid = courses.sid "
                + "WHERE students.sid = 1 "
                + "GROUP BY students.name "
                + "HAVING SUM(courses.credit) > 3 "
                + "ORDER BY students.name "
                + "LIMIT 1";
        LogicalPlanner builder = new LogicalPlanner();
        SqlBaseLexer lexer = new SqlBaseLexer(CharStreams.fromString(sqlText.toUpperCase()));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        SqlBaseParser parser = new SqlBaseParser(tokenStream);
        OutputNode plan = (OutputNode) builder.visit(parser.singleStatement());
        plan.printPlan();
        System.out.println();
        Node optimizedPlan = new RBOptimizer(plan).getOptimizedPlan();
        optimizedPlan.printPlan();
    }

    private static void CBOTest() throws Exception {
        String sqlText =
            "SELECT SUM(A.ID), B.NAME "
                + "FROM A LEFT JOIN B ON A.ID = B.ID "
                + "INNER JOIN C ON B.ID = C.ID "
                + "INNER JOIN D ON A.ID = D.ID "
                + "INNER JOIN E ON A.ID = E.ID "
                + "INNER JOIN F ON A.ID = F.ID "
                + "RIGHT JOIN G on B.NAME = G.NAME "
                + "WHERE A.ID > 10 AND B.ID > 10 AND C.ID > 10 "
                + "GROUP BY A.ID, B.ID "
                + "HAVING SUM(A.ID) > 10 "
                + "ORDER BY C.SCORE DESC, D.SCORE "
                + "LIMIT 2";
        LogicalPlanner builder = new LogicalPlanner();
        SqlBaseLexer lexer = new SqlBaseLexer(CharStreams.fromString(sqlText.toUpperCase()));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        SqlBaseParser parser = new SqlBaseParser(tokenStream);
        OutputNode originalLogicalPlan = (OutputNode) builder.visit(parser.singleStatement());
        originalLogicalPlan.printPlan();
        JoinReorder joinReorderRewriter = new JoinReorder(originalLogicalPlan);
        System.out.println(" -------- -------- -------- -------- --------");
        System.out.println("Test message: " + joinReorderRewriter.getJoinPredicates());
    }

    public static void main(String[] args) throws Exception {
        System.out.println("\n-------- Parser Test --------\n"); ParserTest();
        //System.out.println("\n-------- RBO Test --------\n"); RBOTest();
        //System.out.println("\n-------- CBO Test --------\n"); CBOTest();
    }
}