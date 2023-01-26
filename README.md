# SQL Optimizer

> ByteDance 4th Youth Camp | Big Data Track

## Introduction

This is a simple SQL engine supporting CSV data searching, various SQL operators, rule-based and cost-based optimization, and different modes of shuffling. Basic funtionality include:

- Parsing and analyzing SQL to form an abstract syntax tree.
- Supporting various SQL operators including Scan, Project, Filter, Aggregate, Limit, and different modes of Join.
- Supporing various rule-based optimization including predicate pushdown, limit pushdown, constant folding, column pruning, etc.
- Supporting various cost-based optimization including join reorder, hash optimization, shuffle optimization, etc.

## SQL Parser

The SQL parser parses and analyzes an SQL to form an abstract syntax tree. For example, consider the following SQL:
```
SELECT students.name, courses.name
FROM students INNER JOIN courses ON students.sid = courses.sid
WHERE students.sid = 1
GROUP BY students.name
HAVING SUM(courses.credit) > 3
ORDER BY students.name LIMIT 1
```
It can be parsed and analyzed to form the following abstract syntax tree:
```
Output()
-- Limit(1)
-- -- Sort([SortItem(STUDENTS.NAME ASC)])
-- -- -- Project([ProjectItem(NONE STUDENTS.NAME), ProjectItem(NONE COURSES.NAME)])
-- -- -- -- Having([HavingItem(GREATER_THAN 3 ProjectItem(SUM COURSES.CREDIT))])
-- -- -- -- -- GroupBy([GroupByItem(STUDENTS.NAME)])
-- -- -- -- -- -- Filter([FilterItem(STUDENTS.SID EQUAL_TO 1)])
-- -- -- -- -- -- -- Join(INNER STUDENTS.SID COURSES.SID)
-- -- -- -- -- -- -- -- Scan(STUDENTS)
```

## SQL Operators

SQL operators supported include `aggregation`, `filter`, `groupby`, `having`, `join` (including full join, inner join, left join, and right join), `limit`, `project`, `scan`, and `sort`.

*Functionalities are not fully tested and may contain bugs due to limitation of time.*

## Rule-Based Optimization

Our SQL optimizer supports various rule-based optimization including column pruning, filter pushdown, and limit pushdown. For example, consider the following abstract syntax tree:
```
Output()
-- Limit(2)
-- -- Sort([SortItem(C.SCORE DESC), SortItem(D.SCORE ASC)])
-- -- -- Project([ProjectItem(SUM A.ID), ProjectItem(NONE B.NAME)])
-- -- -- -- Having([HavingItem(GREATER_THAN 10 ProjectItem(SUM A.ID))])
-- -- -- -- -- GroupBy([GroupByItem(A.ID), GroupByItem(B.ID)])
-- -- -- -- -- -- Filter([FilterItem(A.ID GREATER_THAN 10), FilterItem(B.ID GREATER_THAN 10), FilterItem(C.ID GREATER_THAN 10)])
-- -- -- -- -- -- -- Join(FULL A.ID D.ID)
-- -- -- -- -- -- -- -- Join(FULL B.ID C.ID)
-- -- -- -- -- -- -- -- -- Join(LEFT A.ID B.ID)
-- -- -- -- -- -- -- -- -- -- Scan(A)
-- -- -- -- -- -- -- -- -- -- Scan(B)
-- -- -- -- -- -- -- -- -- Scan(C)
-- -- -- -- -- -- -- -- Scan(D)
```
It can be optimized by the rule-based optimizer to the following optimized abstract syntax tree:
```
Output()
-- Project([ProjectItem(SUM A.ID), ProjectItem(NONE B.NAME)])
-- -- Limit(2)
-- -- -- Sort([SortItem(C.SCORE DESC), SortItem(D.SCORE ASC)])
-- -- -- -- Having([HavingItem(GREATER_THAN 10 ProjectItem(SUM A.ID))])
-- -- -- -- -- GroupBy([GroupByItem(A.ID), GroupByItem(B.ID)])
-- -- -- -- -- -- Join(FULL A.ID D.ID)
-- -- -- -- -- -- -- Join(FULL B.ID C.ID)
-- -- -- -- -- -- -- -- Join(LEFT A.ID B.ID)
-- -- -- -- -- -- -- -- -- Filter([FilterItem(A.ID GREATER_THAN 10)])
-- -- -- -- -- -- -- -- -- -- Scan(A [ID])
-- -- -- -- -- -- -- -- -- Filter([FilterItem(B.ID GREATER_THAN 10)])
-- -- -- -- -- -- -- -- -- -- Scan(B [NAME, ID])
-- -- -- -- -- -- -- -- Filter([FilterItem(C.ID GREATER_THAN 10)])
-- -- -- -- -- -- -- -- -- Scan(C [SCORE, ID])
-- -- -- -- -- -- -- Scan(D [SCORE, ID])
```

## Cost-Based Optimization

*Cost-based optimizer is uncompleted due to the limitation of time.*
