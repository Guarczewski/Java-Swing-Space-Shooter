import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Objects;
import java.util.Random;

public class Index extends JFrame implements MouseMotionListener {

    protected ImageIcon Player = new ImageIcon(Objects.requireNonNull(getClass().getResource("/Stuff/Spaceship.png"))); // Player Icon Container
    protected ImageIcon Enemy = new ImageIcon(Objects.requireNonNull(getClass().getResource("/Stuff/Spaceship2.png"))); // Enemy Icon Container

    protected static final int BoxWidth = 450, BoxHeight = 800; // Application Size
    protected static final int Magazine = 512, CharCount = 33, BoostCount = 3; // Magazine <-- Amount of Bullets that are rendered. CharCount <-- Amount of Characters that are rendered

    protected static int BulletID = 0, CurrentlyAlive = CharCount; // BulletID <-- ID of currently shot bullets. CurrentlyAlive <-- Amount of Character currently alive

    protected static int RandomBuffEventTiming = 2000; // Every Time When X Ticks Pass Buff Appears

    protected static Character[] Players = new Character[CharCount]; // Array of Characters
    protected static Bullet[] Boosts = new Bullet[BoostCount]; // Array of Bullets
    protected static Bullet[] Bullets = new Bullet[Magazine]; // Array of Bullets

    Index(){
        super("Space Shooter");
        setBounds(0,0,BoxWidth,BoxHeight);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel Game = new JPanel(null);
        Game.setBackground(Color.black);

        Players[0] = new Character(100,Player,false); // Construct Player at particular cords [ South Center ]
        Players[0].SetNewPosition((BoxWidth / 2) - 16, (BoxHeight / 2) - 16);
        Game.add(Players[0]);// Add Player to Game Panel

        for (int i = 1; i < CharCount; i++) {
            Players[i] = new Character(100,Enemy,true); // Construct Enemy at particular cords
            Players[i].LastShotTick = new Random().nextInt(500);
            Players[i].UpdateVelocity(-1, 0);
            Game.add(Players[i]);
        }

        for (int i = 0; i < Magazine; i++) {
            Bullets[i] = new Bullet(); // Construct Bullets
            Game.add(Bullets[i]); // Add Bullets to Panel
        }

        for (int i = 0; i < BoostCount; i++) {
            Boosts[i] = new Bullet(); // Construct Bullets
            Boosts[i].setBackground(Color.WHITE);
            Game.add(Boosts[i]); // Add Bullets to Panel
        }

        Game.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "Space"); // DIY KeyListener for Space
        Game.getActionMap().put("Space", TriggerSpace); // Trigger event on space

        UpdateFormation();

        addMouseMotionListener(this);
        setContentPane(Game);
        setVisible(true);

    }

    public static void UpdateFormation(){
        int XAccess = 1, YAccess = 1; // Temporary Indexes of Players
        for (int i = 1; i < CharCount; i++) {
            Players[i].SetNewPosition(XAccess * 55, YAccess * 65); // Construct Enemy at particular cords
            Players[i].UpdateVelocity(-1, 0);
            XAccess++; // Increase X Access by 1
            if (XAccess >= 8) { // If there is more than 8 enemies in row
                YAccess++; // Increase Y Access by 1
                XAccess = 1; // Reset X Access
            }
        }
    }

    public static void NewRound(){
        for (int i = 1; i < CharCount; i++) { Players[i].Revive(); }
        CurrentlyAlive = CharCount; // Reset Currently Alive
        UpdateFormation();
    }

    public static void Shoot(int Source){

        if (Players[Source].IsAlive()) { // Check if Sender is alive
            int Center = 0;

            for (int i = 0; i < Players[Source].MagazineSize; i++) {

                if (Bullets[BulletID].InMagazine) { // Check if Bullet is free to use
                    Bullets[BulletID].InMagazine = false; // Pull Bullet out of magazine

                    int CorrectedPlayerX = (Players[Source].getX() + 16) + (Center * 2);

                    if (Source == 0) {
                        Bullets[BulletID].UpdateBullet(Source,Center,-20,CorrectedPlayerX,Players[Source].getY() - 16, new Dimension(1,32));
                        Bullets[BulletID].setBackground(Color.GREEN); // Set Color [ Green <-- Is reserved for Player ]
                    } else {
                        Bullets[BulletID].UpdateBullet(Source,Center,+20,CorrectedPlayerX,Players[Source].getY() + 16, new Dimension(1,32));
                        Bullets[BulletID].setBackground(Color.RED); // Set Color [ Red <-- Is reserved for Enemies ]
                    }
                    BulletID++; // Update BulletID
                    if (BulletID >= Magazine) // If BulletID is higher than amount of bullets in magazine reset BulletID
                        BulletID = 0;
                    }

                    if (i == 0) { Center = -1; }
                    else if (i % 2 != 0) { Center *= -1; }
                    else { Center *= -1; Center += -1; }

                }
        }
    }

    public static Index MainWindow;

    public static void main(String[] args) {

        MainWindow = new Index();

        boolean ErrorFree = true;

        int CurrentBuffTick = 0;
        int CurrentTick = 0;

        while (ErrorFree) {

            CurrentTick++;

            for (int i = 1; i < CharCount; i++) {
                if (Players[i].IsAlive()) {
                    if (CurrentTick - Players[i].LastShotTick > 200) {
                        Players[i].LastShotTick = CurrentTick;
                        Shoot(i);
                    }
                    if (!new Rectangle(MainWindow.getBounds()).intersects(new Rectangle(Players[i].getX() + Players[i].VelocityX,Players[i].getY() + Players[i].VelocityY,32,32))){
                        Players[i].UpdateVelocity((Players[i].VelocityX * -1), 0);
                    }
                    Players[i].UpdatePosition(); // Update Character Position
                }
            }

            for (int i = 0; i < Magazine; i++) {
                if (!Bullets[i].InMagazine) {
                    Bullets[i].UpdatePosition();
                    for (int j = 0; j < CharCount; j++) {
                        if ((Bullets[i].SourceID != 0 && j == 0) || (Bullets[i].SourceID == 0 && j != 0)) {
                            if (new Rectangle(Players[j].getBounds()).intersects(new Rectangle(Bullets[i].getBounds()))){
                                Players[j].TakeDamage(Players[Bullets[i].SourceID].Damage);
                                Bullets[i].StopBullet();
                                if (!Players[j].IsAlive()) {
                                    Players[j].Kill();
                                    CurrentlyAlive--;
                                } // Check If Player Is Alive
                            } // Check For Hit boxes
                        } // Check For Bullet Source
                    } // Check For Every Character
                } // Check If Bullet Isn't In Magazine
            } // Check For Every Bullet

            if (CurrentTick - CurrentBuffTick > RandomBuffEventTiming) {
                int x = new Random().nextInt(3);
                if (Boosts[x].InMagazine) {
                    Boosts[x].UpdateBullet(1, 0, +2, new Random().nextInt(BoxWidth) + (BoxWidth / 4), 0, new Dimension(32, 32));
                    Boosts[x].UpdatePosition();
                    Boosts[x].InMagazine = false;
                    CurrentBuffTick = CurrentTick;
                }
            }

            for (int i = 0; i < BoostCount; i++) {
                if (!Boosts[i].InMagazine) {
                    Boosts[i].UpdatePosition();
                    if (new Rectangle(Players[0].getBounds()).intersects(new Rectangle(Boosts[i].getBounds()))){
                        if (i == 0) { Players[0].Damage++; }
                        else if (i == 1) { Players[0].MagazineSize++; }
                        else { Players[0].CurrentHealth += 20;
                            if (Players[0].CurrentHealth > Players[0].MaxHealth) {
                                Players[0].CurrentHealth = Players[0].MaxHealth;
                            }
                            Players[0]._HealthBar.UpdateCurrentHealth(Players[0].CurrentHealth);
                        }
                        Boosts[i].StopBullet();
                    } // Check For Hit boxes
                } // Check If Bullet Isn't In Magazine
            } // Check For Every Bullet

            if (CurrentlyAlive == 1) // If only player left, trigger new Round;
                NewRound();

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                ErrorFree = false;
            }
        }
    }

    Action TriggerSpace= new AbstractAction() { public void actionPerformed(ActionEvent e) { Shoot(0); } }; // Call Shoot function when Space event got triggered

    @Override
    public void mouseDragged(MouseEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (Players[0].IsAlive()) {




            Players[0].setBounds(e.getX(), e.getY(), 32, 32);

        }
    } // Teleport Player to mouse

}
