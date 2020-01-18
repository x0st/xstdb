.PHONY: compile spawn-socket-worker spawn-query-runner-worker

compile:
	test -d build || mkdir build
	javac \
		-cp libs/mappedbus-0.5.1.jar:src/main/java \
		-d build \
		src/main/java/database/QueryRunnerWorker.java \
		src/main/java/database/SocketWorker.java

spawn-socket-worker:
	java -cp build:libs/mappedbus-0.5.1.jar database.SocketWorker

spawn-query-runner-worker:
	java -cp build database.QueryRunnerWorker

test-create-table:
	echo "add users [id INTEGER] [name STRING]" | netcat --tcp localhost 5555