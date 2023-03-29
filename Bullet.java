import javax.swing.*;
import java.awt.*;

public class Bullet extends JPanel {

    protected static final int BoxHeight = 720;
    protected int BULLET_WIDTH;
    protected int BULLET_HEIGHT;

    public int SourceID;

    public boolean InMagazine;

    protected int VelocityX, VelocityY;

    Bullet(){ InMagazine = true; SourceID = 0; VelocityX = 0; VelocityY = 0; }

    public void UpdateBullet(int _SourceID, int _VelocityX, int _VelocityY, int _CordX, int _CordY, Dimension _Size){
        SourceID = _SourceID;
        VelocityX = _VelocityX;
        VelocityY = _VelocityY;
        BULLET_WIDTH = _Size.width;
        BULLET_HEIGHT = _Size.height;
        this.setBounds(_CordX,_CordY,BULLET_WIDTH,BULLET_HEIGHT);
    }

    public void UpdatePosition(){
        this.setBounds(this.getX() + VelocityX,this.getY() + VelocityY,BULLET_WIDTH,BULLET_HEIGHT);

        if (this.getY() > BoxHeight || this.getY() < 0)
            StopBullet();
    }

    public void StopBullet(){
        VelocityX = 0;
        VelocityY = 0;
        SourceID = 0;
        InMagazine = true;
        this.setBounds(-32,-32,BULLET_WIDTH,BULLET_HEIGHT);
    }

}
