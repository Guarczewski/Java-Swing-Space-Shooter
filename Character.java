import javax.swing.*;
import java.awt.*;

class HealthBar extends JLabel {

    protected int MaxHealth;
    protected int CurrentHealth;
    protected Dimension Dimensions;
    HealthBar(int _MaxHealth, Dimension _Dimensions){
        Dimensions = _Dimensions;
        this.setPreferredSize(Dimensions);
        MaxHealth = CurrentHealth = _MaxHealth;
        this.setForeground(Color.GREEN);
        this.setBackground(Color.RED);
    }

    public void UpdateCurrentHealth(int _CurrentHealth){
        CurrentHealth = _CurrentHealth;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        double tmp = (Dimensions.width * ( 1 - (0.01 * (MaxHealth - CurrentHealth))));
        g.fillRect(0,0,(int)tmp,this.getHeight());
    }

}

public class Character extends JPanel {

    public static final byte CHAR_SIZE = 32;

    public int LastShotTick = 0, MagazineSize = 1;

    public int MaxHealth, CurrentHealth, Damage;

    protected int VelocityX, VelocityY;

    public HealthBar _HealthBar;

    Character(int _MaxHealth, ImageIcon _Icon, boolean _BarOnTop){

        this.setLayout(new BorderLayout());

        this.setBounds(0,0,CHAR_SIZE,CHAR_SIZE + 4);

        MaxHealth = CurrentHealth = _MaxHealth;

        Damage = 10;

        _HealthBar = new HealthBar(MaxHealth,new Dimension(CHAR_SIZE,4));

        if (_BarOnTop) this.add(_HealthBar,BorderLayout.NORTH);
        else this.add(_HealthBar,BorderLayout.SOUTH);

        this.add(new JPanel(){ public void paintComponent(Graphics g) { super.paintComponent(g); setForeground(Color.BLACK); g.fillRect(0,0,32,32); g.drawImage(_Icon.getImage(), 0, 0, null); }}, BorderLayout.CENTER);

        UpdatePosition();
    }

    public void UpdatePosition(){
        this.setBounds(this.getX() + VelocityX, this.getY() + VelocityY, CHAR_SIZE, CHAR_SIZE);
    }

    public void SetNewPosition(int _CordX, int _CordY){
        this.setBounds(_CordX, _CordY, CHAR_SIZE, CHAR_SIZE);
    }

    public void Revive(){
        CurrentHealth = MaxHealth;
        _HealthBar.UpdateCurrentHealth(CurrentHealth);
    }

    public void Kill(){
        this.setBounds(0, 0, 0, 0);
    }

    public void UpdateVelocity( int _VelocityX, int _VelocityY){
        VelocityX = _VelocityX;
        VelocityY = _VelocityY;
    }

    public boolean IsAlive(){
        return CurrentHealth > 0;
    }

    public void TakeDamage(int _Damage){
        CurrentHealth -= _Damage;
        _HealthBar.UpdateCurrentHealth(CurrentHealth);
    }

}
