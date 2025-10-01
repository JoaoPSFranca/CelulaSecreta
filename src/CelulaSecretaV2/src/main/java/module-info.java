module br.edu.ifsp.pep {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;

    exports br.edu.ifsp.pep;
    exports br.edu.ifsp.pep.controller to javafx.fxml;

    opens br.edu.ifsp.pep.controller to javafx.fxml;
}