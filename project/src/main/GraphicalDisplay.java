package main;

import java.io.IOException;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import sim.Simulation;
import javafx.scene.input.MouseEvent;

/**
 * Main class launching a graphical display associated with a simulation. 
 * This class creates the simulation and updates it periodically until 
 * the simulation terminates.
 * 
 * DO NOT EDIT THIS CLASS. Edit the Simulation class instead.
 * 
 * This class is based on the Java FX toolkit included in Oracle Java.
 * 
 * @author t.perennou, MOREIRA Gabriel
 *
 */
public class GraphicalDisplay extends Application {

	/** Refresh image period (milliseconds) */
	static public final Duration PERIOD_MS = Duration.millis(1500 / Simulation.UPDATE_RATE_HZ);

	/** The simulation that contains and updates elements to display */
	private Simulation simulation;

	/** Root of the Java FX scene graph containing all the elements to display */
	private Group root;

	/** Serves as a clock that periodically triggers simulation updates */
	private Timeline timeline;
	
	private double  xOffset;
    private double  yOffset;

	/**
	 * Initialize the graphical display.
	 * 
	 * In a Java FX application, this is a mandatory replacement of the constructor.
	 * @throws IOException 
	 */
	@Override
	public void start(Stage primaryStage) throws IOException {
		// Create the root of the graph scene. It is mainly used in the updateScene() method.
		root = new Group();

		// Create a simulation with N elements
		simulation = new Simulation();

		// Configure and start periodic scene update: after PERIOD_MS ms, updateScene() is called.
		timeline = new Timeline(new KeyFrame(PERIOD_MS, ae -> {
			updateScene();
		})); // "->" is a Java 8 specific construction
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();


		/** Allows the window to be dragged by the mouse */
	    root.setOnMousePressed(new EventHandler<MouseEvent>() {
	    	@Override
	        public void handle(MouseEvent event) {
	             xOffset = primaryStage.getX() - event.getScreenX();
	             yOffset = primaryStage.getY() - event.getScreenY();
	        }
	    });	        
	    root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	primaryStage.setX(event.getScreenX() + xOffset);
	            primaryStage.setY(event.getScreenY() + yOffset);
	        }
	    });
	        
	        
	    /** Adds the stylesheet for visual effect */
	    root.getStylesheets().add("/stylesheet.css");
	    root.getStyleClass().add("rootPane");
	    
	    /** Adds icon */
	    primaryStage.getIcons().add(new Image("/icon.png"));
	  
	    Scene scene = new Scene(root, Simulation.SPACE_SIZE, Simulation.SPACE_SIZE, Simulation.BACKGROUND);                
	    primaryStage.setScene(scene);
     	primaryStage.setTitle("Gabriel's Land");
     	primaryStage.setResizable(false);
	    primaryStage.getScene().setCursor(Cursor.HAND);
	    primaryStage.show();

	}

	/**
	 * Update the Java FX scene graph
	 * @throws IOException 
	 */
	public void updateScene() {
		// check whether updates should be stopped
		if (simulation.isTerminated()) { 
			timeline.stop();
			simulation.exit();
		} else {
			// first update the elements coordinates
			try {
				simulation.update();
			} catch (IOException e) {
				System.out.println("Simulation> Non-critical error: Couldn't communicate with Server.");
			}

			// then update the scene graph (in a rather brutal way)
			root.getChildren().clear();
			root.getChildren().addAll(simulation.getElements());
		}
	}

	/**
	 * Application main method: launch() creates an instance of this class and calls
	 * start().
	 * 
	 * @param args  dUnused
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
