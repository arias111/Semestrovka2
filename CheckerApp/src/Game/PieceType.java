package Game;

public enum PieceType {
    BLACK(1), WHITE(-1);

    final int moveDir;

    PieceType(int moveDir) {
        this.moveDir = moveDir;
    }
    public String getColor(){
        if (moveDir == 1) return "Black";
        else return "White";
    }

    public static PieceType getType(String color){
        if (color.equals("Black")) return BLACK;
        else return WHITE;
    }
}
