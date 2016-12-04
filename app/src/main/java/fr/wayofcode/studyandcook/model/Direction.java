package fr.wayofcode.studyandcook.model;

/**
 * Created by jeremy on 28/09/16.
 */

public class Direction {

    private String title;
    private String counter;


    public Direction(String title) {
        this(title,null);

    }
    public Direction( String title, String counter) {
        super();
        this.title = title;
        this.counter = counter;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
