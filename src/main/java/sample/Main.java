package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;


import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {

    private Label cpuUtil = new Label();

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Drawing a Circle
        Circle circle = new Circle();

        //Setting the properties of the circle
        circle.setCenterX(300.0f);
        circle.setCenterY(135.0f);
        circle.setRadius(100.0);

        CPUCircle circle1 = new CPUCircle();
        cpuUtil.setLayoutX(295);
        cpuUtil.setLayoutY(250);
        //Setting the properties of the circle
        circle1.setCenterX(300.0f);
        circle1.setCenterY(135.0f);
        circle1.setRadius(0.0f);
        circle1.setFill(Color.RED);

        Group root = new Group(circle, circle1, cpuUtil);

        //Creating a scene object
        Scene scene = new Scene(root, 600, 300);
        //Setting title to the Stage
        primaryStage.setTitle("CPU Utilization");

        //Adding scene to the stage
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        //Displaying the contents of the stage
        primaryStage.show();
        startTask(circle1);
    }

    private void startTask(CPUCircle cpuCircle) {
        DBManager dbManager = new DBManager();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int x = (int) cpuCircle.getRadius();
                int y = getCPUUsage();
                insertValue(dbManager, y);
                updateLabel(dbManager);
                cpuCircle.changeSize(x, y);
            }
        }, 0, 1000);
    }

    private void insertValue(DBManager dbManager, int y) {
        try {
            dbManager.insertCPUUtil(y);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateLabel(DBManager dbManager) {
        Platform.runLater(() -> {
            try {
                cpuUtil.setText(dbManager.receiveLastRecordInserted());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private int getCPUUsage() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();

        System.out.println(hal.getProcessor().getSystemCpuLoad() * 100);
        return (int) (hal.getProcessor().getSystemCpuLoad() * 100);

    }

    public static void main(String[] args) {
        launch(args);
    }
}

