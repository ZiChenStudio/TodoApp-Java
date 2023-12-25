import javax.swing.JOptionPane;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
class TodoItem {
    private String name;
    private LocalDateTime time;
    public TodoItem(String name, LocalDateTime time) {
        this.name = name;
        this.time = time;
    }
    public String getName() {
        return name;
    }
    public LocalDateTime getTime() {
        return time;
    }
}
public class TodoApp {
    private static final String FILENAME = "todo.txt";
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm";
    private ArrayList<TodoItem> todoList = new ArrayList<>();
    public TodoApp() {
        loadTodoList();
    }
    private void loadTodoList() {
        File file = new File(FILENAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0];
                    LocalDateTime time = LocalDateTime.parse(parts[1], DateTimeFormatter.ISO_DATE_TIME);
                    TodoItem item = new TodoItem(name, time);
                    todoList.add(item);
                } else {
                    System.out.println("Invalid format in todo.txt: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveTodoList() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
            StringBuilder sb = new StringBuilder();
            for (TodoItem item : todoList) {
                sb.append(item.getName())
                    .append(",")
                    .append(item.getTime().format(DateTimeFormatter.ISO_DATE_TIME))
                    .append("\n");
            }
            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addTodoItem(String name, LocalDateTime time) {
        TodoItem newItem = new TodoItem(name, time);
        todoList.add(newItem);
        saveTodoList();
    }
    public void checkReminders() {
        while (true) {
            LocalDateTime now = LocalDateTime.now();
            for (TodoItem item : new ArrayList<>(todoList)) {
                if (now.isAfter(item.getTime())) {
                    JOptionPane.showMessageDialog(null, "Reminder: It's time for " + item.getName());
                    int index = todoList.indexOf(item);
                    todoList.remove(index);
                    saveTodoList();
                    if (todoList.isEmpty()) {
                        System.out.println("All todos completed!");
                        System.exit(0);
                    }
                }
            }
            try {
                Thread.sleep(1000 * 15);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    public static void main(String[] args) {
        TodoApp todoApp = new TodoApp();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.print("Enter the todo item (or type 'exit' to finish): ");
                String todo = reader.readLine();
                if (todo.equals("exit")) {
                    System.out.println("Start running.");
                    if (todoApp.todoList.isEmpty()) {
                        System.out.println("All todos completed!");
                        System.exit(0);
                    } else {
                        break;
                    }
                }
                System.out.print("Enter the time for the todo item (YYYY-MM-DD HH:MM): ");
                String timeString = reader.readLine();
                LocalDateTime time = LocalDateTime.parse(timeString, DateTimeFormatter.ofPattern(TIME_FORMAT));
                todoApp.addTodoItem(todo, time);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        todoApp.checkReminders();
    }
}
