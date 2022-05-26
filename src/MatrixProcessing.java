import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatrixProcessing {

  private static final Pattern REGEX_FOR_MATRIX_VALIDATION =
      Pattern.compile(
          "^[ \\t]*([A-Z])[ \\t]*=[ \\t]*\\[((([ \\t]*-?\\d+[ \\t]*)+;)*([ \\t]*-?\\d+[ \\t]*)+)][ \\t]*$");

  private static final Pattern REGEX_FOR_OPERATION_VALIDATION =
      Pattern.compile("^([ \\t]*[A-Z][ \\t]*[+,*,-])*([ \\t]*[A-Z][ \\t]*){1}$");

  private static final String OPERATION_MULTIPLICATION = "*";
  private static final String OPERATION_MINUS = "-";
  private static final String OPERATION_PLUS = "+";

  private static final String READ_MATRIX_EXCEPTION_MESSAGE = "read matrix";
  private static final String READ_OPERATION_EXCEPTION_MESSAGE = "read operation line";
  private static final String MULTIPLICATION_OPERATION_EXCEPTION_MESSAGE = "perform multiplication";
  private static final String SUBTRACTION_OPERATION_EXCEPTION_MESSAGE = "perform subtraction";
  private static final String ADDITION_OPERATION_EXCEPTION_MESSAGE = "perform addition";

  private static long[][] answerMatrix = null;
  private static long[][] tempMatrix = null;

  public static void main(String[] args) {

    Scanner scanner = new Scanner(System.in);
    Map<String, long[][]> matrixMap = new HashMap<>();
      List<String> fileLines = new ArrayList<>();

    String[] operationLine;

    readFullFile(scanner, fileLines);
    readAllMatrix(fileLines, matrixMap);
    operationLine = readOperation(fileLines);
    matrixJuggleryAlgorithm(operationLine, matrixMap);
    convertFromMatrixToLine(answerMatrix);

    scanner.close();
  }

  public static void readFullFile(Scanner scanner, List<String> listOfLines) {
    while (scanner.hasNext()) {
      listOfLines.add(scanner.nextLine());
    }
  }

  public static void readAllMatrix(List<String> fileLines, Map<String, long[][]> matrixMap) {

    Matcher matcher;

    for (int i = 0; i < fileLines.size() - 2; i++) {
      matcher = REGEX_FOR_MATRIX_VALIDATION.matcher(fileLines.get(i));
      if (matcher.matches()) {
        if (matrixMap.containsKey(matcher.group(1))) {
          messageException(READ_MATRIX_EXCEPTION_MESSAGE);
        } else {
          matrixMap.put(matcher.group(1), convertFromLineToMatrix(matcher.group(2)));
        }
      } else {
        messageException(READ_MATRIX_EXCEPTION_MESSAGE);
      }
    }
  }

  public static String[] readOperation(List<String> fileLines) {
    Matcher matcher = REGEX_FOR_OPERATION_VALIDATION.matcher(fileLines.get(fileLines.size() - 1));
    if (!matcher.matches()) {
      messageException(READ_OPERATION_EXCEPTION_MESSAGE);
    }
    return ("+" + fileLines.get(fileLines.size() - 1).trim().replaceAll(" +", "") + "+").split("");
  }

  public static void matrixJuggleryAlgorithm(
      String[] operationLine, Map<String, long[][]> matrixMap) {

    boolean isEmptyTemp = true;
    String currentOperation = null;

    for (int i = 0; i < operationLine.length; i++) {
      if (operationLine[i].equals(OPERATION_MINUS) || operationLine[i].equals(OPERATION_PLUS)) {
        currentOperation = operationLine[i];
      }
      if (operationLine[i].matches("[A-Z]")) {
        if (!operationLine[i + 1].equals(OPERATION_MULTIPLICATION)) {
          if (isEmptyTemp) {
            plusMinusMatrixFunction(matrixMap.get(operationLine[i]), currentOperation);
          } else {
            tempMatrix = multiMatrixFunction(tempMatrix, matrixMap.get(operationLine[i]));
            plusMinusMatrixFunction(tempMatrix, currentOperation);
            isEmptyTemp = true;
            makeZeroMatrix(tempMatrix);
          }
        } else {
          if (isEmptyTemp) {
            tempMatrix = copyMatrix(matrixMap.get(operationLine[i]));
          } else {
            tempMatrix = multiMatrixFunction(tempMatrix, matrixMap.get(operationLine[i]));
          }
          isEmptyTemp = false;
        }
      }
    }
  }

  public static long[][] copyMatrix(long[][] matrixToCopy) {

    long[][] newMatrix = new long[matrixToCopy.length][matrixToCopy[0].length];

    for (int i = 0; i < matrixToCopy.length; i++) {
      System.arraycopy(matrixToCopy[i], 0, newMatrix[i], 0, matrixToCopy[0].length);
    }
    return newMatrix;
  }

  public static void makeZeroMatrix(long[][] matrixToZero) {
    for (int i = 0; i < matrixToZero.length; i++) {
      for (int j = 0; j < matrixToZero[0].length; j++) {
        matrixToZero[i][j] = 0;
      }
    }
  }

  public static long[][] convertFromLineToMatrix(String matrixLine) {

    String[] lines = matrixLine.split(";");
    int amountOfColumns =
        (int) lines[0].trim().replaceAll(" +", " ").chars().filter(c -> c == (int) ' ').count() + 1;
    int amountOfLines = lines.length;

    String[][] lineMatrix = new String[amountOfLines][amountOfColumns];
    long[][] matrix = new long[amountOfLines][amountOfColumns];

    for (int i = 0; i < amountOfLines; i++) {
      lineMatrix[i] = lines[i].trim().replaceAll(" +", " ").split(" ");
    }

    for (int i = 0; i < amountOfLines; i++) {
      for (int j = 0; j < amountOfColumns; j++) {
        matrix[i][j] = Long.parseLong(lineMatrix[i][j]);
      }
    }

    return matrix;
  }

  public static void convertFromMatrixToLine(long[][] matrix) {

    StringBuilder stringBuilder = new StringBuilder().append("[");
    String tempString;

    for (int i = 0; i < matrix.length - 1; i++) {
      tempString = Arrays.toString(matrix[i]).replaceAll(",", "");
      stringBuilder.append(tempString.subSequence(1, tempString.length() - 1)).append("; ");
    }
    tempString = Arrays.toString(matrix[matrix.length - 1]).replaceAll(",", "");
    stringBuilder.append(tempString.subSequence(1, tempString.length() - 1)).append("]");

    System.out.println(stringBuilder);
  }

  public static long[][] multiMatrixFunction(long[][] matrixA, long[][] matrixB) {

    if (matrixA[0].length != matrixB.length) {
      messageException(MULTIPLICATION_OPERATION_EXCEPTION_MESSAGE);
    }

    int m = matrixA.length;
    int n = matrixB[0].length;
    int o = matrixB.length;
    long[][] res = new long[m][n];

    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        for (int k = 0; k < o; k++) {
          res[i][j] += matrixA[i][k] * matrixB[k][j];
        }
      }
    }
    return res;
  }

  public static void plusMinusMatrixFunction(long[][] matrix, String operation) {

    if (answerMatrix == null) {
      answerMatrix = copyMatrix(matrix);
      makeZeroMatrix(answerMatrix);
    }

    if (answerMatrix.length != matrix.length || answerMatrix[0].length != matrix[0].length) {
      if (operation.equals(OPERATION_MINUS)) {
        messageException(SUBTRACTION_OPERATION_EXCEPTION_MESSAGE);
      } else {
        messageException(ADDITION_OPERATION_EXCEPTION_MESSAGE);
      }
    }

    if (operation.equals(OPERATION_PLUS)) {
      for (int i = 0; i < answerMatrix.length; i++) {
        for (int j = 0; j < answerMatrix[0].length; j++) {
          answerMatrix[i][j] += matrix[i][j];
        }
      }
    }

    if (operation.equals(OPERATION_MINUS)) {
      for (int i = 0; i < answerMatrix.length; i++) {
        for (int j = 0; j < answerMatrix[0].length; j++) {
          answerMatrix[i][j] -= matrix[i][j];
        }
      }
    }
  }

  private static void messageException(String operationExceptionMessage) {
    System.err.printf(
        "Exception caught: %s. Can't %s.%n",
        IllegalArgumentException.class.getSimpleName(), operationExceptionMessage);
    System.exit(0);
  }
}
