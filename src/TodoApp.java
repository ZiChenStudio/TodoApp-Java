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
    private ArrayList<TodoItem> todoList;

    public TodoApp() {
        todoList = new ArrayList<>();
        loadTodoList();
    }

    private void loadTodoList() {
        try {
            File file = new File(FILENAME);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(FILENAME));
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
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTodoList() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME));
            for (TodoItem item : todoList) {
                writer.write(item.getName() + "," + item.getTime().format(DateTimeFormatter.ISO_DATE_TIME) + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addTodoItem(String name, LocalDateTime time) {
        TodoItem todoItem = new TodoItem(name, time);
        todoList.add(todoItem);
        saveTodoList();
    }

    public void checkReminders() {
        while (true) {
            LocalDateTime now = LocalDateTime.now();
            for (TodoItem item : new ArrayList<>(todoList)) {
                if (now.isAfter(item.getTime())) {
                    try {
                        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", "msg", "*", "Reminder: It's time for " + item.getName());
                        pb.start();
                        int index = todoList.indexOf(item);
                        todoList.remove(index);
                        saveTodoList();
                        if (todoList.isEmpty()) {
                            System.out.println("All todos completed!");
                            System.exit(0);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                Thread.sleep(60000);
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
                    if (todoApp.todoList.isEmpty()) {
                        System.out.println("All todos completed!");
                        System.exit(0);
                    } else {
                        break;
                    }
                }

                System.out.print("Enter the time for the todo item (YYYY-MM-DD HH:MM): ");
                String timeString = reader.readLine();
                LocalDateTime time = LocalDateTime.parse(timeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                todoApp.addTodoItem(todo, time);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        todoApp.checkReminders();
    }
}
