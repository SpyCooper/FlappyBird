import javax.swing.JFrame;

public class App
{
    public static void main(String[] args) throws Exception
    {
        // creates the window
        int windowWidth, windowHeight;
        windowWidth = 360;
        windowHeight = 640;
        JFrame window = new JFrame("Flappy Bird");
        window.setSize(windowWidth, windowHeight);
        // makes the window close when the x button is clicked
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // makes the window not resizable
        window.setResizable(false);
        // centers the window
        window.setLocationRelativeTo(null);

        // creates the game
        FlappyBird flappyBird = new FlappyBird();
        window.add(flappyBird);
        // sets the window to the preferred size of the game
        window.pack();
        // this is needed to make the key listener work
        flappyBird.requestFocus();

        // makes the window visible
        window.setVisible(true);

    }
}
