package Model;

import java.util.function.Function;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class ActionButtonTableCell<S> extends TableCell<S, Button> {

    private final Button actionButton;

    public ActionButtonTableCell(String label, Function< S, S> function) {
        this.getStyleClass().add("action-button-table-cell");

        this.actionButton = new Button(label);
        this.actionButton.setOnAction((ActionEvent e) -> {
            function.apply(getCurrentItem());
        });
        this.actionButton.setMaxWidth(Double.MAX_VALUE);
    }

    public ActionButtonTableCell(String label,boolean checking,Function< S, S> function) {
        this.getStyleClass().add("action-button-table-cell");

        this.actionButton = new Button(label);
        this.actionButton.setOnAction((ActionEvent e) -> {
            function.apply(getCurrentItem());
        });
        this.actionButton.setMaxWidth(Double.MAX_VALUE);
//        this.actionButton.setDisable(true);

        if(checking==false){
            this.actionButton.setDisable(true);
        }else{
            this.actionButton.setDisable(false);
        }
    }

    public S getCurrentItem() {
        return (S) getTableView().getItems().get(getIndex());
    }

    public static <S> Callback<TableColumn<S, Button>, TableCell<S, Button>> forTableColumn(String label,boolean checking, Function< S, S> function) {
        return param -> new ActionButtonTableCell<>(label, checking,function);
    }

    public static <S> Callback<TableColumn<S, Button>, TableCell<S, Button>> forTableColumn(String label, Function< S, S> function) {
        return param -> new ActionButtonTableCell<>(label, function);
    }

    @Override
    public void updateItem(Button item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(actionButton);
        }
    }
}