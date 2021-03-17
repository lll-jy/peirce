package logic;

public enum Language {
    Coq, LaTeX;


    @Override
    public String toString() {
        return switch (this) {
            case Coq -> "Coq";
            case LaTeX -> "LaTeX";
        };
    }
}
