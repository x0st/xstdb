package query.maker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.DataType;
import database.exception.BadQueryException;
import database.query.QueryIdentifier;
import database.query.entity.CreateTableQuery;
import database.query.maker.CreateTableQueryMaker;
import database.query.parser.Lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateTableQueryMakerTester {
    private CreateTableQueryMaker mMaker;
    private Lexer mLexer;
    private QueryIdentifier mQueryIdentifier;

    @BeforeEach
    public void instantiateDependencies() {
        mQueryIdentifier = new QueryIdentifier();
        mMaker = new CreateTableQueryMaker();
        mLexer = new Lexer();
    }

    @Test
    public void throwsExceptionToIncorrectSyntax00() throws BadQueryException {
        mLexer.setInput("add users [".toCharArray());
        mQueryIdentifier.identify(mLexer);

        Assertions.assertThrows(BadQueryException.class, () -> mMaker.make(mLexer));
    }

    @Test
    public void throwsExceptionToIncorrectSyntax01() throws BadQueryException {
        mLexer.setInput("add users".toCharArray());
        mQueryIdentifier.identify(mLexer);

        Assertions.assertThrows(BadQueryException.class, () -> mMaker.make(mLexer));
    }

    @Test
    public void throwsExceptionToIncorrectSyntax02() throws BadQueryException {
        mLexer.setInput("add users []".toCharArray());
        mQueryIdentifier.identify(mLexer);

        Assertions.assertThrows(BadQueryException.class, () -> mMaker.make(mLexer));
    }

    @Test
    public void throwsExceptionToIncorrectSyntax03() throws BadQueryException {
        mLexer.setInput("add users [id] [id]".toCharArray());
        mQueryIdentifier.identify(mLexer);

        Assertions.assertThrows(BadQueryException.class, () -> mMaker.make(mLexer));
    }

    @Test
    public void throwsExceptionToIncorrectSyntax04() throws BadQueryException {
        mLexer.setInput("add users [id TYPE]".toCharArray());
        mQueryIdentifier.identify(mLexer);

        Assertions.assertThrows(BadQueryException.class, () -> mMaker.make(mLexer));
    }

    @Test
    public void throwsExceptionToIncorrectSyntax05() throws BadQueryException {
        mLexer.setInput("add users [id TYPE".toCharArray());
        mQueryIdentifier.identify(mLexer);

        Assertions.assertThrows(BadQueryException.class, () -> mMaker.make(mLexer));
    }

    @Test
    public void throwsExceptionToIncorrectSyntax06() throws BadQueryException {
        mLexer.setInput("add users [id TYPE]".toCharArray());
        mQueryIdentifier.identify(mLexer);

        Assertions.assertThrows(BadQueryException.class, () -> mMaker.make(mLexer));
    }

    @Test
    public void throwsExceptionToIncorrectSyntax07() throws BadQueryException {
        mLexer.setInput("add users [id STRING] [name TYPE]".toCharArray());
        mQueryIdentifier.identify(mLexer);

        Assertions.assertThrows(BadQueryException.class, () -> mMaker.make(mLexer));
    }

    @Test
    public void throwsExceptionToIncorrectSyntax08() throws BadQueryException {
        mLexer.setInput("add users [id STRING] [name]".toCharArray());
        mQueryIdentifier.identify(mLexer);

        Assertions.assertThrows(BadQueryException.class, () -> mMaker.make(mLexer));
    }

    @Test
    public void createsCorrectly00() throws BadQueryException {
        mLexer.setInput("add users [id INTEGER] [name STRING]".toCharArray());
        mQueryIdentifier.identify(mLexer);

        CreateTableQuery query = mMaker.make(mLexer);

        assertEquals("users", query.getTableName());
        assertEquals(2, query.getTableScheme().getColumns().length);

        assertEquals("id", String.valueOf(query.getTableScheme().getColumns()[0].getName()));
        assertEquals(DataType.INTEGER, query.getTableScheme().getColumns()[0].getType());

        assertEquals("name", String.valueOf(query.getTableScheme().getColumns()[1].getName()));
        assertEquals(DataType.STRING, query.getTableScheme().getColumns()[1].getType());
    }

    @Test
    public void createsCorrectly01() throws BadQueryException {
        mLexer.setInput("add users [id INTEGER]".toCharArray());
        mQueryIdentifier.identify(mLexer);

        CreateTableQuery query = mMaker.make(mLexer);

        assertEquals("users", query.getTableName());
        assertEquals(1, query.getTableScheme().getColumns().length);

        assertEquals("id", String.valueOf(query.getTableScheme().getColumns()[0].getName()));
        assertEquals(DataType.INTEGER, query.getTableScheme().getColumns()[0].getType());
    }

    @Test
    public void createsCorrectly02() throws BadQueryException {
        mLexer.setInput("add users [id INTEGER] [bla INTEGER]".toCharArray());
        mQueryIdentifier.identify(mLexer);

        CreateTableQuery query = mMaker.make(mLexer);

        assertEquals("users", query.getTableName());
        assertEquals(2, query.getTableScheme().getColumns().length);

        assertEquals("id", String.valueOf(query.getTableScheme().getColumns()[0].getName()));
        assertEquals(DataType.INTEGER, query.getTableScheme().getColumns()[0].getType());

        assertEquals("bla", String.valueOf(query.getTableScheme().getColumns()[1].getName()));
        assertEquals(DataType.INTEGER, query.getTableScheme().getColumns()[1].getType());
    }

    @Test
    public void createsCorrectly03() throws BadQueryException {
        mLexer.setInput("add users [id STRING] [bla STRING]".toCharArray());
        mQueryIdentifier.identify(mLexer);

        CreateTableQuery query = mMaker.make(mLexer);

        assertEquals("users", query.getTableName());
        assertEquals(2, query.getTableScheme().getColumns().length);

        assertEquals("id", String.valueOf(query.getTableScheme().getColumns()[0].getName()));
        assertEquals(DataType.STRING, query.getTableScheme().getColumns()[0].getType());

        assertEquals("bla", String.valueOf(query.getTableScheme().getColumns()[1].getName()));
        assertEquals(DataType.STRING, query.getTableScheme().getColumns()[1].getType());
    }
}
