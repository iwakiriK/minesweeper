

interface MineSweeperGUI {
    public void setTextToTile(int x, int y, String text);
    public void win();
    public void lose();
}

public class MineSweeper {

    private final int height;
    private final int width;
    private final int numberOfTiles;
    private final int numberOfBombs;
    private final int[][] table;
    private int reamingTiles;        // 残りのタイルの数
    private boolean isFirstAction;     // 最初の一手かどうか
    private boolean[][] openedTiles; // 開けられたタイルかどうか
    private boolean[][] flag;        // フラグがあるかどうか
    private boolean isBusy;          // 処理中かどうか


    public MineSweeper(int height, int width, int numberOfBombs) {
        this.height = height;
        this.width = width;
        this.numberOfTiles = height * width;
        this.numberOfBombs = numberOfBombs;
        this.table = new int[height][width];
	this.reamingTiles = this.numberOfTiles - this.numberOfBombs;
	this.isFirstAction = true;
	this.openedTiles = new boolean[height][width];
	this.flag = new boolean[height][width];
	this.isBusy = false;
        initTable();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    void initTable() {
        setBombs();
	for (int y = 0; y < height; y++) {
	    for (int x = 0; x < width; x++) {
		if (this.table[y][x] != -1) continue; // 爆弾マスならば下の処理を行う
		for (int dy = y-1; dy <= y+1; dy++) {
		    for (int dx = x-1; dx <= x+1; dx++) {
			if (dy == y && dx == x) continue; // 周辺8マスならば下が処理される
			if (0 <= dx && dx < width && 0 <= dy && dy < height) {
			    if (this.table[dy][dx] != -1) {
				this.table[dy][dx]++;
			    }
			}
		    }
		}
	    }
	}
    }

    // table中にnumberOfBombsの値だけランダムに爆弾を設置する．
    void setBombs() {
	int remaining = this.numberOfBombs; // 爆弾の残りの数
	while(remaining != 0) {
	    int x = new java.util.Random().nextInt(width);  // x座標
	    int y = new java.util.Random().nextInt(height); // y座標
	    if (this.table[y][x] == 0) {
		this.table[y][x] = -1; // 爆弾を-1とおく
		remaining--;
	    }
	}
    }

    // 1手目が爆弾だった場合
    public void firstAction(int dx, int dy, int x, int y) {
	// 別のマスに爆弾を移す
	for (int ny = dy-1; ny <= dy+1; ny++) {
	    for (int nx = dx-1; nx <= dx+1; nx++) {
		if(ny == dy && nx == dx) continue;
		if (0 <= nx && nx < width && 0 <= ny && ny < height) {
		    if(this.table[ny][nx] != -1) this.table[ny][nx]++;
		}
	    }
	}
	this.table[dy][dx] = -1;

	// 一手目のマスを爆弾でないようにする
	int count = 0;
	this.table[y][x] = 0;
	for (int ny = y-1; ny <= y+1; ny++) {
	    for (int nx = x-1; nx <= x+1; nx++) {
		if(ny == y && nx == x) {
		    continue;
		}
		if (0 <= nx && nx < width && 0 <= ny && ny < height) {
		    if(this.table[ny][nx] != -1) {
			this.table[ny][nx]--;
		    } else {
			count++;
		    }
		}
	    }
	}
	this.table[y][x] = count;
    }

    // 左クリックしたときに呼び出される．
    public void openTile(int x, int y, MineSweeperGUI gui) {
	if (isBusy) return; // 処理中ならば処理しない

	if (this.isFirstAction) {
	    isBusy = true;
	    // 最初の一手の場合
	    while (this.table[y][x] == -1) {
		// 爆弾でないマスを探し，そこに爆弾を移す
		int dx = new java.util.Random().nextInt(width);
		int dy = new java.util.Random().nextInt(height);
		if (this.table[dy][dx] != -1) {
		    this.firstAction(dx, dy, x, y);
		}
	    }
	    this.isFirstAction = false;
	    this.isBusy = false;
	    this.openTile(x, y, gui);
	} else if (this.openedTiles[y][x]) {
	    // 開けられたマスなら，何もしない
	} else if (this.flag[y][x]) {
	    // フラグが立っている場合, 何もしない
	} else if (this.table[y][x] < 0) {
	    // 爆弾だった場合
	    this.openAllTiles(gui);
	    gui.lose();
	} else {
	    // 地雷でない場合
	    if (this.table[y][x] == 0) {
		gui.setTextToTile(x, y, " ");
	    } else {
		gui.setTextToTile(x, y, "" + this.table[y][x]);
	    }
	    this.reamingTiles--;
	    this.openedTiles[y][x] = true;
	    // 0ならば，周辺も開く
	    if (this.table[y][x] == 0) {
		for (int dy = y-1; dy <= y+1; dy++) {
		    for (int dx = x-1; dx <= x+1; dx++) {
			if (dy == y && dx == x) continue;
			if (dy < 0 || dx < 0 || dy >= height || dx >= width) continue;
			this.openTile(dx, dy, gui);
		    }
		}
	    }
	    
	    // 地雷が無い場合
	    if (this.reamingTiles == 0) {
		this.openAllTiles(gui);
		gui.win();
	    }
	}
    }
    // 右クリックされたときに呼び出される．
    public void setFlag(int x, int y, MineSweeperGUI gui) {
	if (this.openedTiles[y][x]) {
	    // 既に開けられたマスは何もしない
	} else if(this.flag[y][x]) {
	    gui.setTextToTile(x, y, "");
	    this.flag[y][x] = false;
	} else {
	    gui.setTextToTile(x, y,"F");
	    this.flag[y][x] = true;
	}
    }

    private void openAllTiles(MineSweeperGUI gui) {
	for (int y = 0; y < height; y++) {
	    for (int x = 0; x < width; x++) {
		openedTiles[y][x] = true;
		if (this.table[y][x] == -1) {
		    gui.setTextToTile(x, y, "B");
		} else {
		    if (this.table[y][x] == 0) {
			gui.setTextToTile(x, y, " ");
		    } else {
			gui.setTextToTile(x, y, "" + this.table[y][x]);
		    }
		}
	    }
	}
    }

    public void openTileWithHint(MineSweeperGUI gui) {
	if (this.reamingTiles == 0) return;

	for (int y = 0; y < height; y++) {
	    for (int x = 0; x < width; x++) {
		if (this.openedTiles[y][x]) continue;
		if (this.table[y][x] != -1) {
		    this.openTile(x, y, gui);
		    return;
		}
	    }
	}
    }
}
