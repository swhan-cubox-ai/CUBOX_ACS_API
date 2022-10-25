package aero.cubox.api.domain.enums;

public enum EnumYN implements EnumModel {

  Y("Y"),
  N("N")
  ;

  private String description;

  EnumYN(String description) {
    this.description = description;
  }

  @Override
  public String getKey() {
    return name();
  }

  @Override
  public String getValue() {
    return description;
  }
}
