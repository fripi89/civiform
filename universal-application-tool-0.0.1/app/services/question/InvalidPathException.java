package services.question;

import services.Path;

public class InvalidPathException extends Exception {
  public InvalidPathException(Path pathName) {
    super("Path not found: " + pathName.path());
  }
}
