import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class TreeTableDemo extends Application {
    private static final String COMPUTER_DEPARTMENT = "Computer Department";
    private static final String SALES_DEPARTMENT = "Sales Department";
    private static final String IT_DEPARTMENT = "IT Department";
    private static final String HR_DEPARTMENT = "HR Department";

    @Override
    public void start(Stage primaryStage) throws Exception {

        JFXTreeTableColumn<User, String> deptColumn = new JFXTreeTableColumn<>("Department");
        deptColumn.setPrefWidth(150);
        deptColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, String> param) -> {
            if (deptColumn.validateValue(param)) {
                return param.getValue().getValue().department;
            } else {
                return deptColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<User, String> empColumn = new JFXTreeTableColumn<>("Employee");
        empColumn.setPrefWidth(150);
        empColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, String> param) -> {
            if (empColumn.validateValue(param)) {
                return param.getValue().getValue().userName;
            } else {
                return empColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<User, String> ageColumn = new JFXTreeTableColumn<>("Age");
        ageColumn.setPrefWidth(150);
        ageColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<User, String> param) -> {
            if (ageColumn.validateValue(param)) {
                return param.getValue().getValue().age;
            } else {
                return ageColumn.getComputedValue(param);
            }
        });


        ageColumn.setCellFactory((TreeTableColumn<User, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        ageColumn.setOnEditCommit((CellEditEvent<User, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition()
                        .getRow())
                .getValue().age.set(t.getNewValue()));

        empColumn.setCellFactory((TreeTableColumn<User, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        empColumn.setOnEditCommit((CellEditEvent<User, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition()
                        .getRow())
                .getValue().userName.set(t.getNewValue()));

        deptColumn.setCellFactory((TreeTableColumn<User, String> param) -> new GenericEditableTreeTableCell<>(
                new TextFieldEditorBuilder()));
        deptColumn.setOnEditCommit((CellEditEvent<User, String> t) -> t.getTreeTableView()
                .getTreeItem(t.getTreeTablePosition()
                        .getRow())
                .getValue().department.set(t.getNewValue()));


        // data
        ObservableList<User> users = FXCollections.observableArrayList();
        users.add(new User(COMPUTER_DEPARTMENT, "23", "CD 1"));
        users.add(new User(SALES_DEPARTMENT, "22", "Employee 1"));
        users.add(new User(SALES_DEPARTMENT, "24", "Employee 2"));
        users.add(new User(SALES_DEPARTMENT, "25", "Employee 4"));
        users.add(new User(SALES_DEPARTMENT, "27", "Employee 5"));
        users.add(new User(IT_DEPARTMENT, "42", "ID 2"));
        users.add(new User(HR_DEPARTMENT, "21", "HR 1"));
        users.add(new User(HR_DEPARTMENT, "28", "HR 2"));

//        for (int i = 0; i < 40000; i++) {
//            users.add(new User(HR_DEPARTMENT, Integer.toString(i % 10), "HR 3" + i));
//        }
//        for (int i = 0; i < 40000; i++) {
//            users.add(new User(COMPUTER_DEPARTMENT, Integer.toString(i % 20), "CD 2" + i));
//        }
//
//        for (int i = 0; i < 40000; i++) {
//            users.add(new User(IT_DEPARTMENT, Integer.toString(i % 5), "HR 4" + i));
//        }

        // build tree
        final TreeItem<User> root = new RecursiveTreeItem<>(users, RecursiveTreeObject::getChildren);

        JFXTreeTableView<User> treeView = new JFXTreeTableView<>(root);
        treeView.setShowRoot(false);
        treeView.setEditable(true);
        treeView.getColumns().setAll(deptColumn, ageColumn, empColumn);


//        JFXTreeTableColumn<TableBlob, String> fileColumn = new JFXTreeTableColumn<>("File Name");
//        fileColumn.setPrefWidth(150);
//        fileColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBlob, String> param) -> {
//            if (fileColumn.validateValue(param)) {
//                return param.getValue().getValue().blobName;
//            } else {
//                return fileColumn.getComputedValue(param);
//            }
//        });
//
//        JFXTreeTableColumn<TableBlob, String> dateColumn = new JFXTreeTableColumn<>("Date");
//        dateColumn.setPrefWidth(150);
//        dateColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<TableBlob, String> param) -> {
//            if (dateColumn.validateValue(param)) {
//                return param.getValue().getValue().date;
//            } else {
//                return dateColumn.getComputedValue(param);
//            }
//        });
//
//        dateColumn.setCellFactory((TreeTableColumn<TableBlob, String> param) -> new GenericEditableTreeTableCell<>(
//                new TextFieldEditorBuilder()));
//        dateColumn.setOnEditCommit((CellEditEvent<TableBlob, String> t) -> t.getTreeTableView()
//                .getTreeItem(t.getTreeTablePosition()
//                        .getRow())
//                .getValue().date.set(t.getNewValue()));
//
//        fileColumn.setCellFactory((TreeTableColumn<TableBlob, String> param) -> new GenericEditableTreeTableCell<>(
//                new TextFieldEditorBuilder()));
//        fileColumn.setOnEditCommit((CellEditEvent<TableBlob, String> t) -> t.getTreeTableView()
//                .getTreeItem(t.getTreeTablePosition()
//                        .getRow())
//                .getValue().blobName.set(t.getNewValue()));
//
//        ObservableList<TableBlob> blobs = FXCollections.observableArrayList();
////        users.add(new TreeTableDemo.User(COMPUTER_DEPARTMENT, "23", "CD 1"));
////        users.add(new TreeTableDemo.User(SALES_DEPARTMENT, "22", "Employee 1"));
////        users.add(new TreeTableDemo.User(SALES_DEPARTMENT, "24", "Employee 2"));
////        users.add(new TreeTableDemo.User(SALES_DEPARTMENT, "25", "Employee 4"));
////        users.add(new TreeTableDemo.User(SALES_DEPARTMENT, "27", "Employee 5"));
////        users.add(new TreeTableDemo.User(IT_DEPARTMENT, "42", "ID 2"));
////        users.add(new TreeTableDemo.User(HR_DEPARTMENT, "21", "HR 1"));
////        users.add(new TreeTableDemo.User(HR_DEPARTMENT, "28", "HR 2"));
//        blobs.add(new TableBlob("Testing 1","7/8/2018"));
//        blobs.add(new TableBlob("Testing 2","7/7/2018"));
//
//        final TreeItem<TableBlob> root = new RecursiveTreeItem<>(blobs, RecursiveTreeObject::getChildren);
//
////        JFXTreeTableView<TableBlob> treeView = new JFXTreeTableView<>(root);
//        JFXTreeTableView<TableBlob> treeView = new JFXTreeTableView<>(root);
//        treeView.setShowRoot(false);
//        treeView.setEditable(true);
//        treeView.getColumns().setAll(fileColumn, dateColumn);
//
        FlowPane main = new FlowPane();
        main.setPadding(new Insets(10));
        main.getChildren().add(treeView);


        JFXButton groupButton = new JFXButton("Group");
        groupButton.setOnAction((action) -> new Thread(() -> treeView.group(empColumn)).start());
        main.getChildren().add(groupButton);

        JFXButton unGroupButton = new JFXButton("unGroup");
        unGroupButton.setOnAction((action) -> treeView.unGroup(empColumn));
        main.getChildren().add(unGroupButton);

        JFXTextField filterField = new JFXTextField();
        main.getChildren().add(filterField);

        Label size = new Label();

        filterField.textProperty().addListener((o, oldVal, newVal) -> {
            treeView.setPredicate(userProp -> {
                final User user = userProp.getValue();
                return user.age.get().contains(newVal)
                        || user.department.get().contains(newVal)
                        || user.userName.get().contains(newVal);
            });
        });

        size.textProperty()
                .bind(Bindings.createStringBinding(() -> String.valueOf(treeView.getCurrentItemsCount()),
                        treeView.currentItemsCountProperty()));
        main.getChildren().add(size);

        Scene scene = new Scene(main, 475, 500);
//        scene.getStylesheets().add(TreeTableDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static final class TableBlob extends RecursiveTreeObject<TableBlob> {
        final StringProperty blobName;
        final StringProperty date;

        TableBlob(String blobName,String date){
            this.blobName=new SimpleStringProperty(blobName);
            this.date=new SimpleStringProperty(date);
        }
    }

    private static final class User extends RecursiveTreeObject<User> {
        final StringProperty userName;
        final StringProperty age;
        final StringProperty department;

        User(String department, String age, String userName) {
            this.department = new SimpleStringProperty(department);
            this.userName = new SimpleStringProperty(userName);
            this.age = new SimpleStringProperty(age);
        }
    }
}