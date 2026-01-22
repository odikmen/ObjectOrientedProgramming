# Chess JavaFX Project

This is a Java 17 Maven project using OpenJFX 25. It implements a playable chess game (two players, local) with the following features:

* 8x8 board GUI in JavaFX
* Unicode chess piece symbols
* Move rules for all pieces
* Castling (kingside and queenside)
* En passant
* Pawn promotion (choice dialog)
* Check / checkmate / stalemate detection
* Legal move highlighting and prevention of illegal moves

How to run:

1. Install Java 17+ and Maven.
2. Run: mvn javafx:run

Project structure:

* src/main/java/com/example/chess: Java sources
* pom.xml: Maven build file configured for OpenJFX 25

Notes:

* This is a compact educational implementation meant to be clear and readable.
