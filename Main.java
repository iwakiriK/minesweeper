import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.util.*;


public class Main extends Frame implements WindowListener, MineSweeperGUI {

    private MineSweeper ms;
    private Button[][] tileTable;
    private static final Font f = new Font("serif", Font.BOLD,16);
    private final ResultDialog resultDialog = new ResultDialog(this, "Result");

    public Main() {
        super("MineSweeper");
        ms = new MineSweeper(16, 16, 40); // depth, width, num of bombs
        init();
    }

    public static void main(String[] args) {
        new Main();
    }

    private void init() {
        this.tileTable = new Button[ms.getHeight()][ms.getWidth()];
        this.addWindowListener(this);
        this.setLayout(new GridLayout(ms.getHeight(), ms.getWidth()));
        for (int i = 0; i < ms.getHeight(); i++) {
            for (int j = 0; j < ms.getWidth(); j++) {
                Button tile = new Button();
                tile.setBackground(Color.LIGHT_GRAY);
                tile.setFont(f);
                tile.addMouseListener(new MouseEventHandler(ms, this, j, i));
                tileTable[i][j] = tile;
                this.add(tile);
            }
        }
        this.setSize(50 * ms.getWidth(), 50 * ms.getHeight());
        this.setVisible(true);
    }

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}

    @Override
    public void setTextToTile(int x, int y, String text) {
        this.tileTable[y][x].setLabel(text);
	// 数字の場合
	if (!(text.equals("F") || text.equals("") || text.equals("B"))) {
	    this.tileTable[y][x].setBackground(Color.GRAY);
	    // 数字に応じて文字の色を変える
	    switch (text) {
	    case "1":
		this.tileTable[y][x].setForeground(Color.CYAN);
		break;
	    case "2":
		this.tileTable[y][x].setForeground(Color.GREEN);
		break;
	    case "3":
		this.tileTable[y][x].setForeground(Color.MAGENTA);
		break;
	    case "4":
		this.tileTable[y][x].setForeground(Color.BLUE);
		break;
	    case "5":
		this.tileTable[y][x].setForeground(Color.RED);
		break;
	    case "6":
		this.tileTable[y][x].setForeground(Color.ORANGE);
		break;
	    case "7":
		this.tileTable[y][x].setForeground(Color.PINK);
		break;
	    case "8":
		this.tileTable[y][x].setForeground(Color.LIGHT_GRAY);
		break;
	    }
	    
	} else if (text.equals("F")) { // フラッグ
	    this.tileTable[y][x].setForeground(Color.YELLOW);
	} else if (text.equals("")) {  // 何もない
	    this.tileTable[y][x].setBackground(Color.LIGHT_GRAY);
	} else if (text.equals("B")) { // 爆弾
	    this.tileTable[y][x].setBackground(Color.RED);
	}
    }

    @Override
    public void win() {
        resultDialog.showDialog("Win !!!");
    }

    @Override
    public void lose() {
        resultDialog.showDialog("Lose ...");
    }
}




class MouseEventHandler implements MouseListener {

    MineSweeper ms;
    MineSweeperGUI msgui;
    int x, y;

    MouseEventHandler(MineSweeper ms, MineSweeperGUI msgui, int x, int y) {
        this.ms = ms;
        this.msgui = msgui;
        this.x = x;
        this.y = y;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (e.getButton()) {
        case MouseEvent.BUTTON1: {
            // マウスの左ボタンが押された時
            ms.openTile(x, y, msgui);
        } break;
        case MouseEvent.BUTTON2: {
            // マウスの真ん中のボタンが押された時
	    ms.openTileWithHint(msgui);
        } break;
        case MouseEvent.BUTTON3: {
            // マウスの右ボタンが押された時
            ms.setFlag(x, y, msgui);
        } break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

}


class ResultDialog extends Dialog {

    Label label, label2;
    Button btn, btn2, btn3, btn4;

    public ResultDialog(Frame owner, String title) {
        super(owner, title);
        setLayout(new GridLayout(2, 1));
        Panel p1 = new Panel();
        label = new Label();
        p1.add(label);
        this.add(p1);
        this.setSize(200, 100);
        btn = new Button();
        btn.setLabel("exit");
        btn.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
            });
        Panel p2 = new Panel();
        p2.add(btn);
        this.add(p2);

	Panel p3 = new Panel();
        label2 = new Label();
        p3.add(label2);
        this.add(p3);
        btn2 = new Button();
        btn2.setLabel("continue");
        btn2.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e) {
		new Main();
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
            });
        Panel p4 = new Panel();
        p4.add(btn2);
        this.add(p4);
	
    }

    public void showDialog(String message) {
        Panel p1 = new Panel();
        this.label.setText(message);
        this.setVisible(true);
    }
}

