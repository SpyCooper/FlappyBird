import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.BufferedImage;

/*
 * TODO:
//  * 1. Fix the ground to move with the pipes
//  * 2. Angle the bird when it jumps
//  * 3. rename function names
//  * 4. rename variables
 * 5. add comments
 */

public class FlappyBird extends JPanel implements ActionListener, KeyListener
{
    // Board
    int bgWidth = 360;
    int bgHeight = 640;

    // Images
    Image backgroundImage;
    Image birdImage;
    Image topPipeImage;
    Image bottomPipeImage;
    Image groundImage;

    // Bird variables
    int birdX = bgHeight / 8; // starting position is 1/8 the height of the screen
    int birdY = bgHeight / 2; // center of the screen
    int birdWidth = 34; // bird hitbox
    int birdHeight = 24; // bird hitbox

    // Bird class
    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image image = birdImage;

        Bird(Image image)
        {
            this.image = image;
        }
    }

    // Pipe variables
    int pipeX = bgWidth;
    int pipeY = 0;
    int pipeGap = 175; // gap between top and bottom pipes
    int pipeWidth = 64; // 1/6 the size of the image
    int pipeHeight = 512;

    // Pipe class
    class Pipe {
        int x = bgWidth;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image image;

        boolean passed = false;

        Pipe(Image image)
        {
            this.image = image;
        }
    }

    // Ground variables
    class Ground {
        int x = 0;
        int y = bgHeight - groundImage.getHeight(null);
        int width = groundImage.getWidth(null);
        int height = groundImage.getHeight(null);
        Image image = groundImage;

        Ground(Image image)
        {
            this.image = image;
        }
    }

    // Game Logic
    Bird bird;
    int birdVelocityY = 0;
    int pipeVelocityX = -4;
    int jumpHeight = -12;
    int gravity = 1;
    Timer gameLoopTimer;
    Timer PlacePipesTimer;
    ArrayList<Pipe> pipeObjects;
    ArrayList<Ground> groundObjects;
    Random random = new Random();
    boolean isGameOver = false;
    boolean started = false;
    double score = 0;
    JLabel startingText;

    // Constructor
    FlappyBird()
    {
        // set the size of the JPanel
        setPreferredSize(new Dimension(bgWidth, bgHeight));
        // set the JPanel to focusable so it can listen for key events
        setFocusable(true);
        // add the key listener to the JPanel
        addKeyListener(this);

        // load the images
        backgroundImage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        groundImage = new ImageIcon(getClass().getResource("./flappybirdground.png")).getImage();

        // create the bird, pipes, and ground
        bird = new Bird(birdImage);
        pipeObjects = new ArrayList<Pipe>();
        groundObjects = new ArrayList<Ground>();

        // place pipes every 2 seconds
        PlacePipesTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PlacePipes();
            }
        });

        // place 2 ground images to move with the pipes
        for (int i = 0; i < 2; i++)
        {
            Ground ground = new Ground(groundImage);
            ground.x = i * ground.width;
            groundObjects.add(ground);
        }

        // run the game loop 60 times per second
        gameLoopTimer = new Timer(1000 / 60, this);

        // show the starting text in the center of the screen
        startingText = new JLabel("Press SPACE to start");
        startingText.setFont(new Font("Arial", Font.BOLD, 20));
        startingText.setForeground(Color.WHITE);
        startingText.setHorizontalAlignment(SwingConstants.CENTER);
        startingText.setBounds(0, bgHeight / 2 - 200, bgWidth, 20);
        setLayout(null);
        add(startingText);
    }

    // Place pipes on the screen
    private void PlacePipes()
    {
        // randomize the y position of the pipes
        int randomPipeY = (int) (pipeY - pipeHeight/4 -Math.random() * (pipeHeight/2)) - 100;

        // create the top half of the pipe
        Pipe topPipe = new Pipe(topPipeImage);
        topPipe.y = randomPipeY;
        pipeObjects.add(topPipe);

        // create the bottom half of the pipe
        Pipe bottomPipe = new Pipe(bottomPipeImage);
        bottomPipe.y = randomPipeY + pipeHeight + pipeGap;
        pipeObjects.add(bottomPipe);
    }

    // Draw the images on the screen
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Draw(g);
    }

    // Draw the images on the screen
    private void Draw(Graphics g)
    {
        // background image
        g.drawImage(backgroundImage, 0, 0, bgWidth, bgHeight, null);

        // pipes
        for (Pipe pipe : pipeObjects)
        {
            g.drawImage(pipe.image, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // ground
        for (Ground ground : groundObjects)
        {
            g.drawImage(ground.image, ground.x, ground.y, ground.width, ground.height, null);
        }

        // bird image
        g.drawImage(bird.image, bird.x, bird.y, bird.width, bird.height, null);

        // score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        // show the score and game over message
        if (isGameOver)
        {
            // show the game over message in the center of the screen
            int x = (bgWidth - metrics.stringWidth("Game Over")) / 2;
            int y = metrics.getHeight() + 175;
            g.drawString("Game Over", x, y);

            // show the score under the game over message
            x = (bgWidth - metrics.stringWidth("Score: " + (int) score)) / 2;
            y = metrics.getHeight() * 2 +175;
            g.drawString("Score: " + (int) score, x, y);

            // show the restart message in a smaller font
            g.setFont(new Font("Arial", Font.BOLD, 20));
            metrics = g.getFontMetrics(g.getFont());
            x = (bgWidth - metrics.stringWidth("Press SPACE to restart")) / 2;
            y = metrics.getHeight() * 4+10 +175;
            g.drawString("Press SPACE to restart", x, y);
        }
        else
        {
            // show the score in the center at the top of the screen
            int x = (bgWidth - metrics.stringWidth("" + (int) score)) / 2;
            int y = metrics.getHeight();
            g.drawString("" + (int) score, x, y);
        }
    }

    // Rotate an image by an angle
    private Image RotateImage(Image image, int angle)
    {
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        Image newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) ((BufferedImage) newImage).getGraphics();
        g2.rotate(Math.toRadians(angle), w / 2, h / 2);
        g2.drawImage(image, 0, 0, null);
        return newImage;
    }

    // Move the bird and pipes
    private void Move()
    {
        // apply gravity to the bird
        birdVelocityY += gravity;

        // angle the bird's image when it jumps
        if (birdVelocityY < -2)
        {
            bird.image = RotateImage(birdImage, Math.max(-25, birdVelocityY * 2));
        } 
        else
        {
            bird.image = RotateImage(birdImage, Math.min(25, birdVelocityY * 2));
        }

        // move the bird
        bird.y += birdVelocityY;
        bird.y = Math.max(bird.y, 0);

        // move the pipes
        for (int i = 0; i < pipeObjects.size(); i++)
        {
            Pipe pipe = pipeObjects.get(i);
            pipe.x += pipeVelocityX;

            // check if the bird has passed the pipe
            if (!pipe.passed && pipe.x + (pipe.width / 2) < bird.x)
            {
                pipe.passed = true;
                score += 0.5; // increment score by 0.5 for each pipe passed (top and bottom)
            }

            // check if the bird is colliding with the pipe
            if (IsColliding(bird, pipe))
            {
                isGameOver = true;
            }

            // remove pipes that are off the screen
            if (pipe.x + pipe.width < 0)
            {
                pipeObjects.remove(i);
                i--; // adjust the index after removal
            }
        }

        // move the ground
        for (int i = 0; i < groundObjects.size(); i++)
        {
            // move the ground
            Ground ground = groundObjects.get(i);
            ground.x += pipeVelocityX;

            // add a new ground image when the first one is off the screen
            if (ground.x + ground.width < 0)
            {
                groundObjects.remove(i);
                Ground newGround = new Ground(groundImage);
                newGround.x = groundObjects.get(groundObjects.size() - 1).x + newGround.width;
                groundObjects.add(newGround);
                i--; // adjust the index after removal
            }
        }

        // check if bird is colliding with the ground
        if (bird.y + bird.height > bgHeight - groundImage.getHeight(null))
        {
            isGameOver = true;
        }
    }

    // Game Loop called every 1/60 seconds
    public void actionPerformed(ActionEvent e)
    {
        // move the bird and pipes
        Move();

        // repaint the screen
        repaint();

        // check if the game is over
        if (isGameOver)
        {
            // stop movement and game
            PlacePipesTimer.stop();
            gameLoopTimer.stop();
        }
    }

    // Check if the bird is colliding with the pipes
    private boolean IsColliding(Bird bird, Pipe pipe)
    {
        return (bird.x < pipe.x + pipe.width && bird.x + bird.width > pipe.x &&
            bird.y < pipe.y + pipe.height && bird.y + bird.height > pipe.y);
    }

    // Restart the game
    private void Restart()
    {
        // reset the bird, pipes, and score
        bird.y = birdY;
        birdVelocityY = 0;
        pipeObjects.clear();
        score = 0;
        isGameOver = false;

        // restart the game loop and pipe placement
        gameLoopTimer.start();
        PlacePipesTimer.start();
    }

    // KeyListener methods
    @Override
    public void keyPressed(KeyEvent e) {
        // When the space bar is pressed, the bird jumps or restarts the game
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            if (isGameOver)
            {
                Restart();
            }
            else if (!started)
            {
                started = true;
                gameLoopTimer.start();
                PlacePipesTimer.start();
                birdVelocityY = jumpHeight;
                // remove the starting text
                remove(startingText);
            }
            else
            {
                birdVelocityY = jumpHeight;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // required to implement KeyListener, but not used
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // required to implement KeyListener, but not used
    }
}
