package database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import database.contract.Query;
import database.contract.QueryExecutor;
import database.contract.QueryMaker;
import database.dagger.AppComponent;
import database.dagger.AppModule;
import database.dagger.DaggerAppComponent;
import database.exception.BadQueryException;
import database.query.QueryIdentifier;
import database.query.parser.Lexer;

public class Engine {
    @Inject List<QueryExecutor> mExecutorList;
    @Inject List<QueryMaker> mQueryMakerList;
    @Inject QueryIdentifier mQueryIdentifier;

    private final Properties mProperties;

    private Engine(Properties properties) {
        mProperties = properties;
    }

    void setupDagger() {
        AppComponent app = DaggerAppComponent.builder()
                .appModule(new AppModule(mProperties))
                .build();

        app.inject(this);
    }

    private Query query(String str) throws BadQueryException {
        Lexer lexer = new Lexer(str.toCharArray());
        QueryType qt = mQueryIdentifier.identify(lexer);

        for (QueryMaker queryMaker : mQueryMakerList) {
            if (queryMaker.supports(qt)) {
                return queryMaker.make(lexer);
            }
        }

        throw BadQueryException.badSyntax();
    }

    private Object execute(Query query) throws BadQueryException, IOException {
        for (QueryExecutor queryExecutor : mExecutorList) {
            if (queryExecutor.executes(query)) {
                return queryExecutor.execute(query);
            }
        }

        throw new RuntimeException();
    }

    void startServer() {
        Query query;
        Socket socket;
        ServerSocket server;
        long time;

        try {
            server = new ServerSocket(Integer.parseInt(mProperties.getProperty("port")));

            try {
                while (true) {
                    socket = server.accept();

                    try {
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        while (true) {
                            String input = bufferedReader.readLine();

                            if (input.equals("exit")) {
                                socket.close();
                                break;
                            }

                            try {
                                time = System.currentTimeMillis();

                                query = query(input);
                                execute(query);

                                bufferedWriter.write(
                                        String.format("\nDone in %sms\n\n", System.currentTimeMillis() - time)
                                );
                            } catch (BadQueryException e) {
                                bufferedWriter.write(
                                        String.format("\nERROR: %s\n\n", e.getMessage())
                                );
                            } catch (IOException e) {
                                bufferedWriter.write("\nInternal error\n\n");
                            } finally {
                                bufferedWriter.flush();
                            }
                        }
                    } finally {
                        socket.close();
                    }
                }
            } finally {
                server.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Engine en;
        String configPath;
        Properties properties;

        try {
            configPath = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("The path to a config file has not been set.");
        }

        validateConfigPath(configPath);

        properties = new Properties();

        try {
            properties.load(new FileReader(configPath));
        } catch (IOException ignore) {
        }

        validateConfigContents(properties);

        en = new Engine(properties);
        en.setupDagger();
        en.startServer();
    }

    private static void validateConfigContents(Properties properties) {
        String bufferSizeProp = properties.getProperty("buffer_size", null);
        String portProp = properties.getProperty("port", null);
        String dirProp = properties.getProperty("dir", null);

        if (null == dirProp) {
            throw new RuntimeException("The dir key has not been set.");
        } else if (!Files.isDirectory(Paths.get(dirProp)) || !Files.isExecutable(Paths.get(dirProp))) {
            throw new RuntimeException("The dir path is invalid.");
        }

        if (null == portProp) {
            throw new RuntimeException("The port key has not been set.");
        }

        if (null == bufferSizeProp) {
            throw new RuntimeException("The buffer_size key has not been set.");
        }

        try {
            Integer.parseInt(bufferSizeProp);
        } catch (NumberFormatException e) {
            throw new RuntimeException("The buffer_size key does not contain a valid integer.");
        }

        try {
            Integer.parseInt(portProp);
        } catch (NumberFormatException e) {
            throw new RuntimeException("The port key does not contain a valid integer.");
        }
    }

    private static void validateConfigPath(String path) {
        Path p = Paths.get(path);

        if (!Files.isRegularFile(p) || !Files.isReadable(p)) {
            throw new InvalidPathException(path, "The given config file does not exist or is not readable.");
        }
    }
}
