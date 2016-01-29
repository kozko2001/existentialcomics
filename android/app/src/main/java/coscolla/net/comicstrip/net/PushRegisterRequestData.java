package coscolla.net.comicstrip.net;

public class PushRegisterRequestData {
  public String token;
  public String proto = "gcm";

  public PushRegisterRequestData(String token) {
    this.token = token;
  }
}
