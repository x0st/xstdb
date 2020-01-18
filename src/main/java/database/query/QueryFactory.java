package database.query;

import database.QueryType;
import database.contract.LexerInterface;
import database.contract.Query;
import database.exception.BadQueryException;
import database.query.assember.CreateTableQueryAssembler;
import database.query.assember.InsertRowsQueryAssembler;
import database.query.assember.DeleteRowsQueryAssembler;
import database.query.assember.DescribeTableQueryAssembler;
import database.query.assember.SelectRowsQueryAssembler;

public class QueryFactory {
    private InsertRowsQueryAssembler mInsertRowsQueryAssembler;
    private DeleteRowsQueryAssembler mDeleteRowsQueryAssembler;
    private SelectRowsQueryAssembler mSelectRowsQueryAssembler;
    private CreateTableQueryAssembler mCreateTableQueryAssembler;
    private DescribeTableQueryAssembler mDescribeTableQueryAssembler;
    private QueryIdentifier mQueryIdentifier;

    public QueryFactory(
            InsertRowsQueryAssembler insertRowsQueryAssembler,
            DeleteRowsQueryAssembler deleteRowsQueryAssembler,
            SelectRowsQueryAssembler selectRowsQueryAssembler,
            CreateTableQueryAssembler createTableQueryAssembler,
            DescribeTableQueryAssembler describeTableQueryAssembler,
            QueryIdentifier queryIdentifier
    ) {
        mInsertRowsQueryAssembler = insertRowsQueryAssembler;
        mDeleteRowsQueryAssembler = deleteRowsQueryAssembler;
        mSelectRowsQueryAssembler = selectRowsQueryAssembler;
        mCreateTableQueryAssembler = createTableQueryAssembler;
        mDescribeTableQueryAssembler = describeTableQueryAssembler;
        mQueryIdentifier = queryIdentifier;
    }

    public Query makeFromLexer(LexerInterface lexer) throws BadQueryException {
        QueryType type = mQueryIdentifier.identify(lexer);

        switch (type) {
            case ADD: return mCreateTableQueryAssembler.assemble(lexer);
            case PUT: return mInsertRowsQueryAssembler.assemble(lexer);
            case GET: return mSelectRowsQueryAssembler.assemble(lexer);
            case SHOW: return mDescribeTableQueryAssembler.assemble(lexer);
            case DELETE: return mDeleteRowsQueryAssembler.assemble(lexer);
            default: throw new RuntimeException("Will never reach here.");
        }
    }
}
