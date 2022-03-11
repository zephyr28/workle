package model;

public enum TileState {
    BLANK (-1),
    ABSENT(0x2B1B),
    CORRECT(0x1F7E9),
    PRESENT(0x1F7E8);

    private final int codepoint;

    TileState(int codepoint) {

        this.codepoint = codepoint;
    }

    public int getCodepoint() {
        return codepoint;
    }
}