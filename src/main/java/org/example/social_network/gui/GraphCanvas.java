package org.example.social_network.gui;
import javafx.collections.ListChangeListener; import javafx.collections.ObservableList; import javafx.scene.canvas.Canvas; import javafx.scene.canvas.GraphicsContext; import javafx.scene.layout.Region; import javafx.scene.paint.Color; import javafx.scene.text.Font; import org.example.social_network.model.FriendShip; import org.example.social_network.model.Profile;
import java.util.HashMap; import java.util.Map;
public class GraphCanvas extends Region {
    private final ObservableList<Profile> profiles;
    private final ObservableList<FriendShip> friendships;
    private final Canvas canvas = new Canvas();

    private static final double NODE_RADIUS = 24;

    public GraphCanvas(
            ObservableList<Profile> profiles,
            ObservableList<FriendShip> friendships
    ) {
        this.profiles = profiles;
        this.friendships = friendships;

        getChildren().add(canvas);
        setMinSize(300, 250);
        setPrefSize(500, 500);

        profiles.addListener(
                (ListChangeListener<Profile>) change -> drawGraph()
        );
        friendships.addListener(
                (ListChangeListener<FriendShip>) change -> drawGraph()
        );

        widthProperty().addListener(
                (obs, oldValue, newValue) -> drawGraph()
        );
        heightProperty().addListener(
                (obs, oldValue, newValue) -> drawGraph()
        );
    }

    @Override
    protected void layoutChildren() {
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
        drawGraph();
    }

    private void drawGraph() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Фон
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);

        if (profiles.isEmpty()) {
            gc.setFill(Color.GRAY);
            gc.setFont(Font.font(15));
            gc.fillText("Нет профилей для отображения", 20, 30);
            return;
        }

        // Рассчитываем позиции вершин по окружности
        Map<Integer, double[]> positions = new HashMap<>();

        double centerX = width / 2;
        double centerY = height / 2;
        double radius = Math.max(
                40,
                Math.min(width, height) * 0.36
        );

        int count = profiles.size();

        for (int i = 0; i < count; i++) {
            Profile profile = profiles.get(i);

            double angle = 2 * Math.PI * i / count - Math.PI / 2;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            positions.put(profile.getId(), new double[]{x, y});
        }

        // Сначала рисуем рёбра, чтобы они были под вершинами
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(2);
        gc.setFont(Font.font(11));

        for (FriendShip friendship : friendships) {
            double[] first = positions.get(friendship.getProfile1());
            double[] second = positions.get(friendship.getProfile2());

            // Пропускаем связи с отсутствующими профилями
            if (first == null || second == null) {
                continue;
            }

            gc.setStroke(Color.LIGHTGRAY);
            gc.setLineWidth(2);
            gc.strokeLine(
                    first[0], first[1],
                    second[0], second[1]
            );

            // Сила связи возле середины ребра
            double middleX = (first[0] + second[0]) / 2;
            double middleY = (first[1] + second[1]) / 2;

            gc.setFill(Color.DARKSLATEGRAY);
            gc.fillText(
                    String.valueOf(friendship.getStrength()),
                    middleX + 4,
                    middleY - 4
            );
        }

        // Затем рисуем вершины
        for (Profile profile : profiles) {
            double[] point = positions.get(profile.getId());
            double x = point[0];
            double y = point[1];

            gc.setFill(Color.LIGHTBLUE);
            gc.fillOval(
                    x - NODE_RADIUS,
                    y - NODE_RADIUS,
                    NODE_RADIUS * 2,
                    NODE_RADIUS * 2
            );

            gc.setStroke(Color.STEELBLUE);
            gc.setLineWidth(2);
            gc.strokeOval(
                    x - NODE_RADIUS,
                    y - NODE_RADIUS,
                    NODE_RADIUS * 2,
                    NODE_RADIUS * 2
            );

            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(12));

            String label = String.valueOf(profile.getId());
            double textWidth = label.length() * 7;

            gc.fillText(
                    label,
                    x - textWidth / 2,
                    y + 4
            );

            gc.setFont(Font.font(10));
            String name = profile.getName();

            if (name != null) {
                gc.fillText(
                        name,
                        x - name.length() * 2.5,
                        y + NODE_RADIUS + 14
                );
            }
        }
    }
    public void refresh() {
        drawGraph();
    }
}