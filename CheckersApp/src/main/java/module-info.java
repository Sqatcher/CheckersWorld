module checkers.checkersapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens checkers.checkersapp to javafx.fxml;
    exports checkers.checkersapp;
}