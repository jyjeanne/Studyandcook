package fr.wayofcode.studyandcook.model;

/**
 * Created by jeremy on 28/09/16.
 */

public class Direction {

  private String descritpion;

  private int order;

  public int getOrder() {
    return order;
  }

  public String getStringOrder() {
    return String.valueOf(order);
  }

  public void setOrder(int order) {
    this.order = order;
  }

  public Direction(String descritpion, int order) {
    super();
    this.descritpion = descritpion;
    this.order = order;
  }

  @Override
  public String toString() {
    return order + "-" + descritpion;
  }

  public String getDescritpion() {
    return descritpion;
  }

  public void setDescritpion(String descritpion) {
    this.descritpion = descritpion;
  }
}
