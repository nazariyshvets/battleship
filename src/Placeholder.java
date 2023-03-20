public enum Placeholder {
    DEFAULT('*'),
    MISS('0'),
    HIT('1'),
    OCCUPIED('-');
    
    private final char placeholder;
    
    private Placeholder(char placeholder) {
        this.placeholder = placeholder;
    }
    
    public char getPlaceholder() {
        return placeholder;
    }
}
