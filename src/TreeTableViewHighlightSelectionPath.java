import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreeTableViewHighlightSelectionPath extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TreeTableView<Item> table = new TreeTableView<Item>();

        PseudoClass ancestorOfSelection = PseudoClass.getPseudoClass("ancestor-of-selection");

        table.setRowFactory(ttv -> new TreeTableRow<Item>() {

            {
                table.getSelectionModel().selectedItemProperty().addListener(
                        (obs, oldSelection, newSelection) -> updateStyleClass());
            }

            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                updateStyleClass();
            }

            private void updateStyleClass() {
                pseudoClassStateChanged(ancestorOfSelection, false);
                TreeItem<Item> treeItem = table.getSelectionModel().getSelectedItem();
                if (treeItem != null) {
                    for (TreeItem<Item> parent = treeItem.getParent(); parent != null; parent = parent.getParent()) {
                        if (parent == getTreeItem()) {
                            pseudoClassStateChanged(ancestorOfSelection, true);
                            break;
                        }
                    }
                }
            }
        });

        TreeTableColumn<Item, String> itemCol = new TreeTableColumn<>("Item");
        itemCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getName()));
        table.getColumns().add(itemCol);

        TreeTableColumn<Item, Number> valueCol = new TreeTableColumn<>("Value");
        valueCol.setCellValueFactory(cellData -> cellData.getValue().getValue().valueProperty());
        table.getColumns().add(valueCol);

        table.setRoot(createRandomTree());

        Scene scene = new Scene(table, 750, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TreeItem<Item> createRandomTree() {
        TreeItem<Item> root = new TreeItem<>(new Item("Item 1", 0));
        Random rng = new Random();
        List<TreeItem<Item>> items = new ArrayList<>();
        items.add(root);

        for (int i = 2; i <= 20; i++) {
            TreeItem<Item> item = new TreeItem<>(new Item("Item " + i, rng.nextInt(1000)));
            items.get(rng.nextInt(items.size())).getChildren().add(item);
            items.add(item);
        }

        return root;
    }

    public static class Item {
        private final String name;
        private final IntegerProperty value = new SimpleIntegerProperty();

        public Item(String name, int value) {
            this.name = name;
            setValue(value);
        }

        public String getName() {
            return name;
        }

        public IntegerProperty valueProperty() {
            return value;
        }

        public final int getValue() {
            return valueProperty().get();
        }

        public final void setValue(int value) {
            valueProperty().set(value);
        }
    }
}