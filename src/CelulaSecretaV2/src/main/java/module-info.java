module br.edu.ifsp.pep.principal.teste2 {
    requires javafx.controls;
    requires javafx.fxml;

    exports br.edu.ifsp.pep;
    exports br.edu.ifsp.pep.controller to javafx.fxml;

    opens br.edu.ifsp.pep.controller to javafx.fxml;
}