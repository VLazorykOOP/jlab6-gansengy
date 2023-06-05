import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.*;
import java.awt.geom.AffineTransform;


public class Main extends JPanel implements Runnable {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    private static final int RECTANGLE_SIZE = 200;
    private static final int DELAY = 10;

    private double angle;

    private static final int FRAME_WIDTH = 500;
    private static final int FRAME_HEIGHT = 400;

    public Main() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);

        angle = 0.0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int x = centerX - RECTANGLE_SIZE / 2;
        int y = centerY - RECTANGLE_SIZE / 2;

        AffineTransform transform = new AffineTransform();
        transform.rotate(angle, centerX, centerY);
        g2d.setTransform(transform);

        g2d.setColor(Color.RED);
        g2d.fillRect(x, y, RECTANGLE_SIZE, RECTANGLE_SIZE);

        g2d.dispose();
    }

    @Override
    public void run() {
        while (true) {
            angle += 0.01;

            repaint();

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void firstTask() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Rotating Rectangle");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Main rotatingRectangle = new Main();
            frame.add(rotatingRectangle);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            Thread animationThread = new Thread(rotatingRectangle);
            animationThread.start();
        });
    }

    private static int[][] readMatrixFromFile(String filePath) throws FileNotFoundException, InvalidMatrixSizeException {
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);

        int n = Integer.parseInt(scanner.nextLine());
        if (n <= 0) {
            throw new InvalidMatrixSizeException("Matrix size must be positive");
        }

        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            String[] values = scanner.nextLine().split(" ");
            if (values.length != n) {
                throw new InvalidMatrixSizeException("Invalid number of elements in row " + (i + 1));
            }

            for (int j = 0; j < n; j++) {
                matrix[i][j] = Integer.parseInt(values[j]);
            }
        }

        return matrix;
    }

    private static String printMatrix(int[][] matrix) {
        StringBuilder builder = new StringBuilder();
        for (int[] row : matrix) {
            for (int value : row) {
                builder.append(value).append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    private static String printVector(int[] vector) {
        StringBuilder builder = new StringBuilder();
        for (int value : vector) {
            builder.append(value).append(" ");
        }
        return builder.toString();
    }

    static class InvalidMatrixSizeException extends Exception {
        public InvalidMatrixSizeException(String message) {
            super(message);
        }
    }

    public static void secondTask() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Matrix Processing");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
            frame.setLayout(new BorderLayout());

            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new FlowLayout());

            JLabel fileLabel = new JLabel("File Path:");
            JTextField fileField = new JTextField(20);
            JButton loadButton = new JButton("Load");
            inputPanel.add(fileLabel);
            inputPanel.add(fileField);
            inputPanel.add(loadButton);

            JTextArea resultArea = new JTextArea();
            resultArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(resultArea);

            frame.add(inputPanel, BorderLayout.NORTH);
            frame.add(scrollPane, BorderLayout.CENTER);

            loadButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String filePath = fileField.getText();
                    try {
                        int[][] X = readMatrixFromFile(filePath);
                        int[][] Y = readMatrixFromFile(filePath);

                        int n = X.length;
                        int[] A = new int[n];

                        for (int i = 0; i < n; i++) {
                            boolean allNegative = true;

                            for (int j = 0; j < n; j++) {
                                if (X[i][j] >= 0 || Y[i][j] >= 0) {
                                    allNegative = false;
                                    break;
                                }
                            }

                            if (allNegative) {
                                A[i] = 1;
                            } else {
                                A[i] = 0;
                            }
                        }

                        StringBuilder resultBuilder = new StringBuilder();
                        resultBuilder.append("Matrix X:\n");
                        resultBuilder.append(printMatrix(X));
                        resultBuilder.append("Matrix Y:\n");
                        resultBuilder.append(printMatrix(Y));
                        resultBuilder.append("The resulting vector is: ");
                        resultBuilder.append(printVector(A));

                        resultArea.setText(resultBuilder.toString());
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(frame, "File not found: " + filePath, "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid input format: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (InvalidMatrixSizeException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid matrix size: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            frame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        System.out.println("Java lab 7");

        char answer;

        do {
            Scanner scanner = new Scanner(System.in);

            System.out.println("1. First task");
            System.out.println("2. Second task");
            System.out.println("E. Exit\n");
            System.out.print("Choose an option:");

            answer = scanner.nextLine().charAt(0);

            switch (Character.toUpperCase(answer)) {
                case '1':
                    firstTask();
                    break;
                case '2':
                    secondTask();
                    break;
                case 'E':
                    System.out.println("Exiting program...");
                    break;
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        } while (Character.toUpperCase(answer) != 'E');
    }
}
