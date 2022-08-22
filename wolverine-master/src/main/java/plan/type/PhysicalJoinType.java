package plan.type;

public enum PhysicalJoinType {
    DUMMY_NESTED_LOOP_JOIN,
    BLOCK_NESTED_LOOP_JOIN,
    SORT_MERGE_JOIN,
    HASH_JOIN
}
