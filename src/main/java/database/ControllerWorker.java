package database;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import database.contract.LexerInterface;
import database.contract.Query;
import database.contract.Tube;
import database.contract.Worker;
import database.exception.BadQueryException;
import database.query.QueryFactory;
import database.query.QueryIdentifier;
import database.query.assember.CreateTableQueryAssembler;
import database.query.assember.DeleteRowsQueryAssembler;
import database.query.assember.DescribeTableQueryAssembler;
import database.query.assember.InsertRowsQueryAssembler;
import database.query.assember.SelectRowsQueryAssembler;
import database.rawquery.LexerFactory;
import database.rawquery.parser.Lexer;

public class SocketWorker implements Worker {
    private QueryFactory mQueryFactory;
    private LexerFactory mLexerFactory;
    private ServerSocket mServer;
    private Tube mTube;

    private SocketWorker(Integer port) throws IOException {
        mServer = new ServerSocket(port);
        mLexerFactory = new LexerFactory();
        mQueryFactory = new QueryFactory(
                new InsertRowsQueryAssembler(),
                new DeleteRowsQueryAssembler(),
                new SelectRowsQueryAssembler(),
                new CreateTableQueryAssembler(),
                new DescribeTableQueryAssembler(),
                new QueryIdentifier()
        );
        mTube = QueryTransport.Builder.forWriting();
    }

    public static void main(String[] args) throws Throwable {
        Integer port = 5555;

        for (int i = 0; i < args.length; i++) {
            if ("--port".equals(args[i])) {
                try {
                    port = Integer.valueOf(args[i + 1]);
                    i++;
                    i++;
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Port must be a number.");
                }
            }
        }

        new SocketWorker(port).run();
    }

    public void run() throws Throwable {
        Socket socket;
        Query query;

        while (true) {
            socket = acceptSocket();

            while (!socket.isClosed()) {
                if (socket.getInputStream().available() > 0) {
                    try {
                        query = parseQuery(socket);
                        mTube.push(query);
                    } catch (BadQueryException e) {
                        handleBadQueryException(socket, e);
                    }
                }
            }
        }
    }

    /**
     * Handle the BadQueryException exception for the given socket connection.
     *
     * @param socket during which connection the exception has occurred
     * @param e      BadQueryException that occurred during parsing query
     */
    private void handleBadQueryException(Socket socket, BadQueryException e) throws Throwable {
        socket.getInputStream().skip(socket.getInputStream().available());

        reply(socket, "Error parsing query");

        System.out.println(String.format("[%s:%s] Error parsing query", socket.getInetAddress().getHostAddress(), socket.getPort()));
    }

    /**
     * Send a message to a socket connection.
     *
     * @param socket  connection to send to
     * @param message what should be sent
     */
    private void reply(Socket socket, String message) throws Throwable {
        PrintWriter writer;

        writer = new PrintWriter(socket.getOutputStream());
        writer.println(message);

        writer.flush();
    }

    /**
     * Cut off a socket connection.
     */
    private void closeSocket(Socket socket) throws Throwable {
        socket.close();

        System.out.println(String.format("[%s:%s] Disconnected", socket.getInetAddress().getHostAddress(), socket.getPort()));
    }

    /**
     * Scan a socket connection for query and if found, try to parse and recognize, and if successful,
     * return a query.
     * An exception is thrown in case a bad query has been provided.
     */
    private Query parseQuery(Socket socket) throws Throwable {
        LexerInterface lexer;
        Query query;

        lexer = mLexerFactory.make(socket.getInputStream());
        query = mQueryFactory.makeFromLexer(lexer);

        System.out.println(String.format("[%s:%s] Sent query", socket.getInetAddress().getHostAddress(), socket.getPort()));

        return query;
    }

    /**
     * Establish a connection with the incoming socket and return it.
     */
    private Socket acceptSocket() throws IOException {
        Socket socket = mServer.accept();

        System.out.println(String.format("[%s:%s] Connected", socket.getInetAddress().getHostAddress(), socket.getPort()));

        return socket;
    }
}
