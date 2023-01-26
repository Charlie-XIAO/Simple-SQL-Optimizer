import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import RBO.RuleBasedOptimizer;
import CBO.JoinReorder;
import parser.SqlBaseLexer;
import parser.SqlBaseParser;
import plan.LogicalPlanner;
import plan.Node;
import plan.OutputNode;
import plan.ScanNode;
import plan.JoinNode;
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
        OutputNode plan = (OutputNode) builder.visit(parser.singleStatement());
        plan.printPlan();
        // Temp test
        Map<Integer, List<Node>> logicalPlan = plan.getLogicalPlan();
        ScanNode scanNode = (ScanNode) logicalPlan.get(8).get(1);
        Iterator<Record> iterator = scanNode.iterator();
        System.out.println(scanNode.records);
        System.out.println(scanNode.table.getSchema());
        System.out.println(scanNode.getStatistics());
    }

    private static void RBOTest() throws Exception {
        String sqlText =
            "SELECT SUM(A.ID), B.NAME "
                + "FROM A LEFT JOIN B ON A.ID = B.ID "
                + "FULL JOIN C ON B.ID = C.ID "
                + "FULL JOIN D ON A.ID = D.ID "
                + "WHERE A.ID > 10 AND B.ID > 10 AND C.ID > 10 "
                + "GROUP BY A.ID, B.ID "
                + "HAVING SUM(A.ID) > 10 "
                + "ORDER BY C.SCORE DESC, D.SCORE "
                + "LIMIT 2";
        LogicalPlanner builder = new LogicalPlanner();
        SqlBaseLexer lexer = new SqlBaseLexer(CharStreams.fromString(sqlText.toUpperCase()));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        SqlBaseParser parser = new SqlBaseParser(tokenStream);
        OutputNode plan = (OutputNode) builder.visit(parser.singleStatement());
        plan.printPlan();
        System.out.println();
        Node optimizedPlan = new RuleBasedOptimizer(plan).getOptimizedPlan();
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
        OutputNode plan = (OutputNode) builder.visit(parser.singleStatement());
        plan.printPlan();
        JoinReorder joinReorderRewriter = new JoinReorder(plan);
        System.out.println(" -------- -------- -------- -------- --------");
        System.out.println("Test message: " + joinReorderRewriter.getJoinPredicates());
    }

    private static void ExecuteTest() throws Exception {
        String sqlText1 =
            "SELECT courses.cid "
                + "FROM enrollments FULL JOIN courses ON enrollments.cid = courses.cid";
        String sqlText2 =
            "SELECT students.sid "
                + "FROM students FULL JOIN courses on students.sid = courses.cid";
        String sqlText3 =
            "SELECT students.sid, courses.cid "
                + "FROM students FULL JOIN enrollments on students.sid = enrollments.sid "
                + "FULL JOIN courses on enrollments.cid = courses.cid ";
        LogicalPlanner builder = new LogicalPlanner();
        SqlBaseLexer lexer = new SqlBaseLexer(CharStreams.fromString(sqlText3.toUpperCase()));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        SqlBaseParser parser = new SqlBaseParser(tokenStream);
        OutputNode plan = (OutputNode) builder.visit(parser.singleStatement());
        Iterator<Record> iterator = plan.iterator();
        int count = 1;
        while (iterator.hasNext()) {
            System.out.print("[" + count + "] ");
            System.out.println(iterator.next());
            count ++;
        }
        plan.printPlan();
        JoinNode joinNode = (JoinNode) plan.getLogicalPlan().get(2).get(0);
        System.out.println("Schema: " + joinNode.table.getSchema());
        System.out.println(joinNode.getStatistics());
        System.out.println(joinNode.records.size() + " records enumerated.");
    }

    public static void main(String[] args) throws Exception {
        //System.out.println("\n-------- Parser Test --------\n"); ParserTest();
        //System.out.println("\n-------- RBO Test --------\n"); RBOTest();
        //System.out.println("\n-------- CBO Test --------\n"); CBOTest();
        System.out.println("\n-------- Execute Test --------\n"); ExecuteTest();
    }

}