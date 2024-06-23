# Default target
# Compile and run
default:
	mvn compile
	java -classpath ./target/classes -XX:+ShowCodeDetailsInExceptionMessages cli.Main

# Compile project with maven to ./target
compile:
	mvn compile

# Run all tests in project
test:
	mvn test

